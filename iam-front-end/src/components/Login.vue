<template>
  <div class="fullscreen">
    <form method="post" :action="backendUrl + '/oauth/login'">
      <md-card class="login-box">
        <img class="logo" src="/static/logo/logo_bright.png">

        <md-card-area>
          <md-card-header>
            <div class="md-title"><span style="font-weight: bold">유엔진</span> 솔루션즈</div>
            <br>
            <div class="md-subhead">Log in to your account</div>
            <div v-if="status == 'fail'" style="color: red">
              Invalid Account
            </div>
          </md-card-header>

          <md-card-content>
            <md-input-container>
              <label>아이디</label>
              <md-input name="userName"
                        type="text"
                        required></md-input>
            </md-input-container>

            <md-input-container>
              <label>패스워드</label>
              <md-input name="userPassword"
                        label="Enter your password"
                        type="password"
                        required></md-input>
            </md-input-container>
          </md-card-content>
        </md-card-area>

        <md-input-container style="display: none">
          <md-input name="authorizeResponse" v-model="authorizeResponse" type="hidden"></md-input>
        </md-input-container>

        <md-card-actions>
          <md-button type="submit">Login</md-button>
        </md-card-actions>
      </md-card>
    </form>
  </div>
</template>

<script>
  export default {
    name: 'Login',
    data: function () {
      return {
        username: null,
        password: null,
        authorizeResponse: null,
        status: null,
        backendUrl: window.backendUrl
      };
    },
    mounted() {
      this.status = this.$route.query['status'];
      this.authorizeResponse = this.$route.query['authorizeResponse'];
    },
    methods: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" rel="stylesheet/scss">
  .fullscreen {
    position: relative;
    height: calc(100vh);
    overflow-y: hidden;
    overflow-x: hidden;
    width: 100%;
    background: #242A37;
    font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  }

  .login-box {
    border-radius: 10px;
    position: relative;
    max-width: 350px;
    margin-left: auto;
    margin-right: auto;
    margin-top: 200px;
    text-align: center;
    overflow: visible;

    .md-card-header {

    }

    .logo {
      position: relative;
      margin-top: -50px;
      margin-left: auto;
      margin-right: auto;
      width: 200px;
    }
  }

</style>
