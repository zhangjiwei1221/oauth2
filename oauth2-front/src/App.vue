<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
export default {
  name: 'App',
  created() {
    this.initCoreSocialistValues()
  },
  methods: {
    initCoreSocialistValues() {
      (function () {
        let playWords = ['富强', '民主', '文明', '和谐', '自由', '平等', '公正', '法制', '爱国', '敬业', '诚信', '友善']
        let colors = ['#e3b4b8', '#baccd9', '#45b787', '#ec2b24']
        let wordIdx = Math.floor(Math.random() * playWords.length)
        document.body.addEventListener('click', function (e) {
          if (e.target.tagName === 'A') {
            return
          }
          const x = e.pageX, y = e.pageY, span = document.createElement('span')
          span.textContent = playWords[wordIdx]
          wordIdx = (wordIdx + 1) % playWords.length
          let color = colors[Math.floor(Math.random() * colors.length)]
          span.style.cssText = `z-index: 9999; position: absolute; top: ${ y - 20}px; left: ${x}px; font-weight: bold; color: ${color}`
          document.body.appendChild(span)
          renderWords(span)
        })
        function renderWords(el) {
          let i = 0, top = parseInt(el.style.top);
          const playTimer = setInterval(function () {
            if (i > 180) {
              clearInterval(playTimer)
              el.parentNode.removeChild(el)
            } else {
              i += 3
              el.style.top = `${top - i}px`
              el.style.opacity = `${(180 - i) / 180}`
            }
          }, 16.7)
        }
      })()
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
