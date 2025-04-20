module.exports = {
    content: [
      "./src/**/*.{cljs,clj,js}", // Сканирует ClojureScript и JS файлы
      "./resources/public/index.html" // HTML-шаблон (если есть)
    ],
    theme: {
      extend: {},
    },
    plugins: [],
  }