# Utiliser une image de base Ubuntu
FROM ubuntu:20.04

# Installer les dépendances nécessaires
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk wget unzip

# Télécharger et installer le SDK Android
RUN mkdir -p /opt/android-sdk && \
    cd /opt/android-sdk && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip commandlinetools-linux-8512546_latest.zip && \
    rm commandlinetools-linux-8512546_latest.zip

# Configurer les variables d'environnement
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin"

# Installer les outils Android
RUN sdkmanager --update && \
    sdkmanager "platform-tools" "platforms;android-30" "build-tools;30.0.3"

# Exposer les ports (si nécessaire)
EXPOSE 8080

# Commande par défaut
CMD ["/bin/bash"]
