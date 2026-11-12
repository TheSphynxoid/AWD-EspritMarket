# ---- Build stage ----
FROM node:24-alpine AS build
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund
COPY . .
RUN npx ng build --configuration production

# ---- Runtime stage : fichiers statiques + reverse proxy (gateway /gw, Keycloak /auth) ----
FROM nginx:1.29-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Supprime la page "Welcome to nginx" (sinon servie sur "/" : le build Angular n'a pas d'index.html racine)
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80
