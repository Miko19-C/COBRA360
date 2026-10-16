#!/bin/bash
set -euo pipefail
export DEBIAN_FRONTEND=noninteractive
apt-get update -qq
apt-get install -y docker.io docker-compose-v2
systemctl enable --now docker
install -d -m 750 -o ubuntu -g ubuntu /opt/cobra360
# Pequeño margen para picos de memoria durante el arranque.
fallocate -l 1G /swapfile
chmod 600 /swapfile
mkswap /swapfile
swapon /swapfile
echo '/swapfile none swap sw 0 0' >> /etc/fstab
# Detener la demo tras 24 horas; el volumen EBS se conserva hasta su eliminación.
shutdown -P +1440
