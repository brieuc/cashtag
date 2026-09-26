#!/usr/bin/env bash
#
# Script de déploiement. Se lance depuis le répertoire home ($HOME).
# Arborescence attendue : ~/cashtag/cashtvue, ~/cashtag/cashtag, ~/nginx-proxy
#
# 1) cashvue (frontend)  : pull + build + restart des containers
# 2) cashtag (backend)   : pull + build + restart des containers
# 3) cashvue puis cashtag : build des deux images, un seul restart (celui de l'option 2)
# 4) nginx-proxy          : restart des containers
# 5) backup DB            : dump de cashtagdb dans /home/debian/backup-cashtagdb
# 6) renouveler le certificat : certbot via docker compose, depuis nginx-proxy, puis restart nginx (option 4)

set -euo pipefail

# Toujours repartir de $HOME, quel que soit l'endroit d'où le script est lancé.
cd "$HOME"

deploy_frontend() {
      local restart="$1" # true|false

      cd cashtag/cashvue
      git pull origin main
      docker build -t cashvue:latest .
      cd ../cashtag

      if [ "$restart" = true ]; then
            docker compose down
            docker compose up -d
      fi

      cd "$HOME"
}

deploy_backend() {
      cd cashtag/cashtag
      git pull origin main
      docker build -t cashtag:latest .
      docker compose down
      docker compose up -d
      cd "$HOME"
}

deploy_nginx() {
      cd nginx-proxy
      docker compose down
      docker compose up -d
      cd "$HOME"
}

backup_database() {
      local backup_dir="/home/debian/backup-cashtagdb"
      local timestamp
      timestamp="$(date +%Y%m%d_%H%M%S)"
      local target="$backup_dir/cashtag_export_${timestamp}.sql"
      local tmp="${target}.part"

      mkdir -p "$backup_dir"

      docker exec mariadb_container mariadb-dump \
            --user root -pBrieucGorin \
            --single-transaction \
            --routines --triggers --events \
            --default-character-set=utf8mb4 \
            cashtagdb > "$tmp"

      # Le fichier final n'apparaît qu'une fois le dump terminé avec succès :
      # si mariadb-dump plante en cours de route, on garde le .part au lieu
      # d'un .sql tronqué qui aurait l'air valide.
      mv "$tmp" "$target"

      echo "Backup enregistré : $target"
}

renew_certificate() {
      cd nginx-proxy
      docker compose --env-file .env run certbot certonly --webroot --webroot-path /var/www/certbot/ -d vps-4ac2e447.vps.ovh.net
      cd "$HOME"

      # Redémarre nginx pour qu'il recharge le certificat renouvelé.
      deploy_nginx
}

echo "Que voulez-vous déployer ?"
echo "  1) cashvue (frontend)"
echo "  2) cashtag (backend)"
echo "  3) cashvue + cashtag (build des deux, un seul restart)"
echo "  4) nginx-proxy"
echo "  5) backup de la base cashtagdb"
echo "  6) renouveler le certificat (certbot)"
read -rp "Choix [1-6] : " choice

case "$choice" in
      1) deploy_frontend true ;;
      2) deploy_backend ;;
      3) deploy_frontend false && deploy_backend ;;
      4) deploy_nginx ;;
      5) backup_database ;;
      6) renew_certificate ;;
      *) echo "Choix invalide : $choice" >&2; exit 1 ;;
esac

echo "Déploiement terminé."
