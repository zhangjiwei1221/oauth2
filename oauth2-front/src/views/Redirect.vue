<template>

</template>

<script>
import {mapMutations} from 'vuex'

export default {
  name: "Redirect",
  created() {
    this.setToken(this.$route.query.token)
    this.$router.push('/')
  },
  methods: {
    ...mapMutations(['setToken'])
  }
}
</script>

<style scoped>

</style>
