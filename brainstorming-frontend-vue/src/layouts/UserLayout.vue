<template>
  <div id="userLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <a-space>
          <img src="../assets/头脑风暴.svg" class="logo" />
          <div style="font-size: 34px">头脑风暴 OJ</div>
        </a-space>
        <div style="margin-top: 6px">尽情发挥你的头脑风暴吧！</div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="https://api.ggbond.online" target="_blank">
          头脑风暴OJ判题系统 by zw
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#userLayout {
  text-align: center;
  background: url("../assets/背景.jpg") 0% 0% / 100% 100%;
}

#userLayout .logo {
  width: 64px;
  height: 64px;
}

#userLayout .header {
  margin-top: 100px;
}

#userLayout .content {
  margin-bottom: 14px;
  padding: 20px;
}

#userLayout .footer {
  padding: 16px;
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
<script></script>
