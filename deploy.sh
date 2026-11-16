#!/usr/bin/env bash
#
# Script de déploiement. Se lance depuis le répertoire home ($HOME).
# Arborescence attendue : ~/cashtag/cashvue, ~/cashtag/cashtag, ~/dailyvue, ~/dailymon, ~/nginx-proxy
#
# 1) cashvue (frontend)     : pull + build + restart des containers
# 2) cashtag (backend)      : pull + build + restart des containers
# 3) cashvue + cashtag      : build des deux images, un seul restart
# 4) dailyvue (frontend)    : pull + build + restart des containers
# 5) dailymon (backend)     : pull + build + restart des containers
# 6) dailyvue + dailymon    : build des deux images, un seul restart
# 7) nginx-proxy            : restart des containers
# 8) backup DB              : dump de cashtagdb dans /home/debian/backup-cashtagdb
# 9) renouveler le certificat : certbot via docker compose, depuis nginx-proxy, puis restart nginx (option 7)

set -euo pipefail

# Toujours repartir de $HOME, quel que soit l'endroit d'où le script est lancé.
cd "$HOME"

# --- Fonctions communes -------------------------------------------------------

# git pull + docker build d'un projet.
# $1 : répertoire du projet (relatif à $HOME), $2 : nom de l'image
pull_and_build() {
      local dir="$1" image="$2"

      cd "$HOME/$dir"
      git pull origin main
      docker build -t "$image:latest" .
      cd "$HOME"
}

# Redémarrage des containers d'un docker-compose.
# $1 : répertoire contenant le docker-compose.yml, $2 (optionnel) : fichier d'environnement
compose_restart() {
      local dir="$1" env_file="${2:-}"
      local args=(-f docker-compose.yml)

      if [ -n "$env_file" ]; then
            args+=(--env-file "$env_file")
      fi

      cd "$HOME/$dir"
      docker compose "${args[@]}" down
      docker compose "${args[@]}" up -d
      cd "$HOME"
}

# --- Applications -------------------------------------------------------------

# Déploie le front, le back ou les deux, avec un seul restart.
# $1 : front|back|both
# $2 : répertoire du front, $3 : image du front
# $4 : répertoire du back (contient le docker-compose.yml), $5 : image du back
# $6 (optionnel) : fichier d'environnement du docker-compose
deploy_app() {
      local mode="$1" front_dir="$2" front_image="$3" back_dir="$4" back_image="$5" env_file="${6:-}"

      case "$mode" in
            front) pull_and_build "$front_dir" "$front_image" ;;
            back)  pull_and_build "$back_dir" "$back_image" ;;
            both)  pull_and_build "$front_dir" "$front_image"
                   pull_and_build "$back_dir" "$back_image" ;;
      esac

      compose_restart "$back_dir" "$env_file"
}

deploy_cashtag() {
      deploy_app "$1" cashtag/cashvue cashvue cashtag/cashtag cashtag
}

deploy_dailymon() {
      deploy_app "$1" dailyvue dailyvue dailymon dailymon .env.prod
}

deploy_nginx() {
      compose_restart nginx-proxy
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

# --- Menu ---------------------------------------------------------------------

echo "Que voulez-vous déployer ?"
echo "  1) cashvue (frontend)"
echo "  2) cashtag (backend)"
echo "  3) cashvue + cashtag (build des deux, un seul restart)"
echo "  4) dailyvue (frontend)"
echo "  5) dailymon (backend)"
echo "  6) dailyvue + dailymon (build des deux, un seul restart)"
echo "  7) nginx-proxy"
echo "  8) backup de la base cashtagdb"
echo "  9) renouveler le certificat (certbot)"
read -rp "Choix [1-9] : " choice

case "$choice" in
      1) deploy_cashtag front ;;
      2) deploy_cashtag back ;;
      3) deploy_cashtag both ;;
      4) deploy_dailymon front ;;
      5) deploy_dailymon back ;;
      6) deploy_dailymon both ;;
      7) deploy_nginx ;;
      8) backup_database ;;
      9) renew_certificate ;;
      *) echo "Choix invalide : $choice" >&2; exit 1 ;;
esac

echo "Déploiement terminé."
