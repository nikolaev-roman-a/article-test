# Stage 1: Build stage
FROM clojure:openjdk-17 AS builder

# Install Node.js and build tools
RUN apt-get update && \
    apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - && \
    apt-get install -y nodejs

WORKDIR /app

# Copy dependency files
COPY deps.edn .
COPY package.json .
COPY package-lock.json .

# Install dependencies
RUN npm ci
RUN clojure -P

# Copy source code
COPY . .

# Build project
RUN clj -T:build all

# Stage 2: Runtime image
FROM clojure:openjdk-17

# Install SQLite and create user
RUN apt-get update && \
    apt-get install -y sqlite3 && \
    groupadd -r clojure && \
    useradd -r -g clojure clojure && \
    mkdir -p /data && \
    chown -R clojure:clojure /data

WORKDIR /app

# Copy built artifacts
COPY --from=builder --chown=clojure:clojure /app/target/articles-standalone.jar .
COPY --from=builder --chown=clojure:clojure /app/target/classes/public/js ./public/js
COPY --from=builder --chown=clojure:clojure /app/resources ./resources

VOLUME /data

# Switch to non-root user
USER clojure

# Run application
CMD ["java", "-jar", "articles-standalone.jar"]