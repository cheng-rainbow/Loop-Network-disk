<template>
  <FrameWork>
    <div class="recycle-nav">
      <div class="recycle-nav-top">
        <div class="nav-top-item">删除文件</div>
        <span class="tip">保存10天内删除的文件</span>
      </div>
      <div class="recycle-nav-bottom">
        <MySpace></MySpace>
      </div>
    </div>
    <div class="recycle-right">
      <RouterView />
    </div>
  </FrameWork>
</template>

<script setup>
import FrameWork from "./FrameWork.vue";
import MySpace from "./MySpace.vue";
</script>

<style lang="scss" scoped>
.recycle-nav {
  width: 200px;
  display: flex;
  padding: 20px 10px 0;
  font-weight: 700;
  flex-direction: column;
  align-content: center;
  justify-content: space-between;
  .recycle-nav-top {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    .nav-top-item {
      width: 180px;
      height: 40px;
      background-color: #eef9fe;
      color: #00b4f6;
      border-radius: 8px;
      text-align: center;
      line-height: 40px;
      user-select: none;
    }
    .tip {
      margin: 10px 0 0 0;
      color: #f56c6c;
      font-size: 13px;
    }
  }
}

.recycle-right {
  width: calc(100% - 200px);
  height: 100%;
  padding: 20px 0 0 20px;
}
</style>
