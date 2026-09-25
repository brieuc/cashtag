#!/usr/bin/env bash
#
# Script de déploiement. Se lance depuis le répertoire home ($HOME).
# Arborescence attendue : ~/cashtag/cashtvue, ~/cashtag/cashtag, ~/nginx-proxy
#
# 1) cashvue (frontend)  : pull + build + restart des containers
# 2) cashtag (backend)   : pull + build + restart des containers
# 3) cashvue puis cashtag : build des deux images, un seul restart (celui de l'option 2)
# 4) nginx-proxy          : restart des containers

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

echo "Que voulez-vous déployer ?"
echo "  1) cashvue (frontend)"
echo "  2) cashtag (backend)"
echo "  3) cashvue + cashtag (build des deux, un seul restart)"
echo "  4) nginx-proxy"
read -rp "Choix [1-4] : " choice

case "$choice" in
      1) deploy_frontend true ;;
      2) deploy_backend ;;
      3) deploy_frontend false && deploy_backend ;;
      4) deploy_nginx ;;
      *) echo "Choix invalide : $choice" >&2; exit 1 ;;
esac

echo "Déploiement terminé."
