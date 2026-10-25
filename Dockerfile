FROM node:22-bookworm-slim AS frontend-build
WORKDIR /workspace
COPY package.json package-lock.json ./
RUN npm ci
COPY app ./app
COPY .openai ./.openai
COPY build ./build
COPY public ./public
COPY worker ./worker
COPY next-env.d.ts next.config.ts postcss.config.mjs tsconfig.json vite.config.ts ./
ENV NEXT_PUBLIC_API_BASE_URL=/api
RUN npm run build

FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /workspace
COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -q
COPY backend/src ./src
RUN ./mvnw package -DskipTests -q

FROM node:22-bookworm-slim AS runtime
COPY --from=eclipse-temurin:21-jre /opt/java/openjdk /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
RUN apt-get update \
    && apt-get install -y --no-install-recommends nginx \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /opt/fitnova
COPY --from=frontend-build /workspace ./web
COPY --from=backend-build /workspace/target/*.jar ./backend.jar
COPY deploy/nginx.conf.template ./nginx.conf.template
COPY deploy/start-production.sh ./start-production.sh
RUN chmod +x ./start-production.sh
ENV PORT=8080
EXPOSE 8080
CMD ["./start-production.sh"]
