<template>
  <div class="my-container">
    <ChangeInfoBox ref="ChildChangeInfoBox"></ChangeInfoBox>
    <ChangePasswordBox ref="ChildChangePasswordBox" />
    <!-- nav 导航栏 -->
    <div class="header">
      <div class="left-logo" @click="toLogin">
        <img class="my-title-icon" src="/svg/Logos.svg" alt="" />
        <!-- <i class="bi bi-cloud-check "></i> -->
        <span class="logo-text">Loop 网盘</span>
      </div>
      <div class="right-info">
        <div @click="onChangeDropdownVisible" ref="arrow" class="right-arrow">
          <img class="arrow-icon" src="/svg/arrow.svg" alt="" />

          <div v-if="uploadFileStore.isDropdownVisible" ref="dropdown" class="upload-box" @click.stop>
            <div class="triangle-1"></div>
            <div class="triangle-2"></div>
            <div class="upload-title">上传任务 (仅展示本次上传任务)</div>
            <div v-if="uploadFileStore.files == null || uploadFileStore.files.length == 0" class="upload-content">暂无上传任务</div>
            <div v-else class="upload-files">
              <div v-for="file in uploadFileStore.files" :key="file.fileId" class="upload-files-item">
                <span>{{ file.fileName }}</span>
                <div class="progress">
                  <div class="bottom">
                    <div :style="{ width: getProgress(file) }" class="top"></div>
                  </div>
                  <div class="span">
                    <span>{{ getProgress(file) }}</span>
                    <div v-if="!file.isFinish && !file.isError" @click="PasueUpload(file)" class="pasue">
                      <i v-if="!file.isPause" title="暂停上传" class="bi bi-pause"></i>
                      <i v-else title="继续上传" class="bi bi-caret-right-fill"></i>
                    </div>
                    <div v-if="!file.isFinish && !file.isError" @click="CancelUpolad(file)" class="cancel-icon" title="取消上传"><i class="bi bi-x"></i></div>
                    <div v-if="file.isFinish || file.isError" @click="CleanUpolad(file)" class="clean">
                      <div class="clean-icon"><i class="bi bi-trash"></i></div>
                      <span>清除记录</span>
                    </div>
                  </div>
                </div>

                <div v-if="file.isFinish == true && file.isError == false" class="ok">
                  <span class="ok-icon">
                    <i class="bi bi-check"></i>
                  </span>
                  <span>上传完成</span>
                </div>
                <div v-else-if="file.isFinish == false && file.isError == false" class="notOk">
                  <span>正在传输</span>
                </div>
                <div v-else-if="file.isError == true" class="error">
                  <span class="error-icon">
                    <i class="bi bi-x"></i>
                  </span>
                  <span>{{ file.errorMessage }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- 弹出框 -->

        <div class="right-user-info">
          <div class="user-avatar">
            <img :src="userStore.user.avatarUrl" alt="" />
            <div class="dropdown">
              <ul>
                <li @click="changePersonInfo">修改个人信息</li>
                <li @click="changePassword">修改密码</li>
                <li @click="userStore.logOut">退出</li>
              </ul>
            </div>
          </div>
          <div class="user-nickname">{{ userStore.user.nickName }}</div>
        </div>
      </div>
    </div>
    <div class="body">
      <div class="left-nav">
        <RouterLink :to="{ path: '/home' }" :class="['left-nav-item', 'left-nav-item1', route.path.includes('/home') ? 'active' : '']">
          <i class="bi bi-house left-nav-icon"></i>
          <span>首页</span>
        </RouterLink>
        <RouterLink :to="{ path: '/share' }" :class="['left-nav-item', route.path.includes('/share') ? 'active' : '']">
          <i class="bi bi-share left-nav-icon"></i>
          <span>分享</span>
        </RouterLink>
        <RouterLink :to="{ path: '/recycle' }" :class="['left-nav-item', route.path.includes('/recycle') ? 'active' : '']">
          <i class="bi bi-recycle left-nav-icon"></i>
          <span>回收站</span>
        </RouterLink>
      </div>
      <div class="right-panel">
        <slot></slot>
      </div>
    </div>
  </div>
  <ErrorAlertBox></ErrorAlertBox>
  <SuccessAlertBox></SuccessAlertBox>
</template>

<script setup>
import { useUserStore } from "@/store/useUserStore";
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import ChangeInfoBox from "./ChangeInfoBox.vue";
import ChangePasswordBox from "./ChangePasswordBox.vue";
import ErrorAlertBox from "@/components/ErrorAlertBox.vue";
import SuccessAlertBox from "@/components/SuccessAlertBox.vue";
import router from "@/router";
import { useUploadFileStore } from "@/store/useUploadFileStore";

const userStore = useUserStore();
const uploadFileStore = useUploadFileStore();

let route = useRoute();

const dropdown = ref(null);
const arrow = ref(null);
const ChildChangeInfoBox = ref(null);
const ChildChangePasswordBox = ref(null);

const PasueUpload = (file) => {
  file.isPause = !file.isPause;
};

const CancelUpolad = (file) => {
  file.isCancel = true;
};

const CleanUpolad = (file) => {
  uploadFileStore.files = uploadFileStore.files.filter((f) => f.fileId != file.fileId);
  return;
};

const getProgress = (file) => {
  if (file.finishChunks == 0) return "0%";
  return `${((file.finishChunks / file.totalChunks) * 100).toFixed(2)}%`;
};

const toLogin = () => {
  router.push({ path: "/home" });
};

const onChangeDropdownVisible = () => {
  uploadFileStore.isDropdownVisible = !uploadFileStore.isDropdownVisible;
};

const onCloseDropdown = (event) => {
  if (arrow.value && !arrow.value.contains(event.target) && dropdown.value && !dropdown.value.contains(event.target)) {
    uploadFileStore.isDropdownVisible = false;
  }
};

const changePersonInfo = () => {
  ChildChangeInfoBox.value.showModal();
};

const changePassword = () => {
  ChildChangePasswordBox.value.showModal();
};

onMounted(() => {
  document.addEventListener("click", onCloseDropdown);
});
onBeforeUnmount(() => {
  document.removeEventListener("click", onCloseDropdown);
});
</script>

<style lang="scss" scoped>
.error-icon {
  width: 16px;
  height: 16px;
  margin-right: 5px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  background-color: red;
}

.error {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: red;
}

.cancel-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 50%;
  background-color: #dff4ff;
  i {
    color: #09a6ff;
    font-size: 13px;
  }
}

.notOk {
  color: #636d7d;
  user-select: none;
}

.ok {
  width: 100%;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: #67c039;
  user-select: none;
  .ok-icon {
    width: 16px;
    height: 16px;
    margin-right: 5px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 16px;
    color: white;
    background-color: #67c039;
  }
}

.upload-files {
  width: 100%;
  height: 90%;
  overflow-y: auto;
  .upload-files-item {
    width: 100%;
    height: 70px;
    padding: 5px 10px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.1);

    display: flex;
    flex-direction: column;
    justify-content: space-around;
    .progress {
      width: 100%;
      height: 16px;
      display: flex;
      align-items: center;
      background-color: white;
      position: relative;
      user-select: none;
      .bottom {
        height: 40%;
        width: 80%;
        background-color: #afb7c3;
        border-radius: 8px;
        .top {
          height: 100%;
          border-radius: 8px;
          background-color: #09a6ff;
        }
      }
      .span {
        width: 20%;
        padding-left: 5px;
        position: absolute;
        left: 80%;
        display: flex;
        align-items: center;
        justify-content: space-around;
        .pasue {
          width: 16px;
          height: 16px;
          border-radius: 50%;
          background-color: #dff4ff;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          i {
            font-size: 16px;
            color: #09a6ff;
          }
        }
        .clean {
          width: 72px;
          height: 16px;
          border-radius: 8px;
          background-color: #e2f5ff;
          display: flex;
          align-items: center;
          justify-content: flex-start;
          cursor: pointer;
          .clean-icon {
            width: 18px;
            height: 18px;
            font-size: 13px;
            color: #09a6ff;
            display: flex;
            align-items: center;
          }
          span {
            color: #09a6ff;
          }
        }
      }
    }
  }
}

.my-title-icon {
  width: 48px;
  height: 48px;
  display: inline;
  margin-right: 10px;
}

.dropdown {
  width: 150px;
  display: none; /* 默认隐藏下拉菜单 */
  position: absolute; /* 绝对定位，放置在父元素下方 */
  top: 46px;
  right: -1px;
  background-color: white; /* 背景色 */
  z-index: 100; /* 确保下拉菜单在最上层 */
  padding-top: 10px;
  ul {
    text-align: center;

    li {
      box-shadow: 1px 1px 5px rgba(0, 0, 0, 0.1);
      margin-bottom: 10px;
    }
  }
}

.user-avatar:hover .dropdown {
  display: flex !important;
  flex-direction: row !important;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  border-radius: 8px;
  box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.1);
}

.dropdown ul {
  list-style: none; /* 去掉列表样式 */
  padding: 0; /* 去掉内边距 */
  margin: 0; /* 去掉外边距 */
}

.dropdown li {
  padding: 10px 15px; /* 内边距 */
  cursor: pointer; /* 鼠标悬浮时显示为手指 */
  border-radius: 8px;
}

.dropdown li:hover {
  background-color: #ecf5ff; /* 悬浮时背景色 */
  color: #3f9eff;
}

.active {
  color: #09a6ff !important;
  background-color: #f1faff !important;
}

.my-container {
  width: 100vw;
  height: 100vh;
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.header {
  width: 100vw;
  height: 56px;
  box-sizing: border-box;
  padding: 0 18px;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  .left-logo {
    cursor: pointer;
    .logo-icon {
      color: #09a6ff;
      font-size: 40px;
    }
    .logo-text {
      font-size: 25px;
      color: #09a6ff;
      margin-left: 5px;
      font-weight: 700;
      // user-select: none;
      // font-size: 25px; /* 可以根据需要调整 */
      // font-weight: bold; /* 加粗字体 */
      // background: linear-gradient(45deg, #fabd05, #fabd05, #33a853, #33a853, #4285f4, #4285f4, #ea4336, #ea4336);
      // -webkit-background-clip: text;
      // -webkit-text-fill-color: transparent; /* 确保字体填充透明 */
    }
  }
  .right-info {
    display: flex;
    align-items: center;
    justify-content: center;
    .right-arrow {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      cursor: pointer;
      .arrow-icon {
        width: 30px;
        height: 30px;
      }
    }
    .right-arrow:hover {
      background-color: #f0f3f7;
      transition: all 0.2s ease-in-out;
    }
    .right-user-info {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-left: 16px;
      cursor: pointer;
      .user-avatar {
        position: relative;
        width: 46px;
        height: 46px;
        // background-color: lightblue;
        img {
          width: 100%;
          height: 100%;
          border-radius: 50%;
        }
      }
      .user-nickname {
        margin-left: 5px;
      }
    }
  }
}

.body {
  display: flex;
  position: relative;
  white-space: nowrap;
  overflow-y: hidden;
  .left-nav {
    width: 80px;
    height: calc(100vh - 56px);
    // background-color: lightblue;
    position: relative;
    box-shadow: 3px 0 10px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    .left-nav-item {
      box-sizing: content-box;
      width: 66px;
      height: 54px;
      padding-bottom: 12px;
      margin-bottom: 12px;
      border-radius: 8px;
      text-decoration: none;
      color: #636d7d;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-items: center;
      font-size: 14px;
      font-weight: 700;
      cursor: pointer;
      .left-nav-icon {
        font-size: 28px;
      }
    }
    .left-nav-item:hover {
      background-color: #f4f5f9;
    }
    .left-nav-item1 {
      margin-top: 24px;
    }
  }

  .right-panel {
    width: calc(100vw - 80px);
    height: calc(100vh - 56px);
    // background-color: #09a6ff;
    position: absolute;
    left: 80px;
    display: flex;
  }
}

.upload-box {
  position: absolute;
  top: 50px; /* 调整显示框的位置 */
  right: -105px;
  width: 800px;
  height: 500px;
  background-color: white;
  border: 1px solid #ddd;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  padding: 10px;
  border-radius: 4px;
  z-index: 500;
  cursor: auto;
}

.upload-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 10px;
  height: 30px;
  border-bottom: solid 1px rgba(0, 0, 0, 0.08);
  user-select: none;
}

.upload-content {
  font-size: 14px;
  color: #999;
  text-align: center;
}

/* 带有三条边的三角形样式 */
.triangle-1 {
  position: absolute;
  top: -10px;
  right: 115px;
  width: 0;
  height: 0;
  border-left: 10px solid transparent; /* 左边框，斜边 */
  border-right: 10px solid transparent; /* 右边框，斜边 */
  border-bottom: 10px solid #000; /* 底部边框 */
}
.triangle-2 {
  position: absolute;
  top: -10px;
  right: 115px;
  width: 0;
  height: 0;
  border-left: 10px solid transparent; /* 左边框，斜边 */
  border-right: 10px solid transparent; /* 右边框，斜边 */
  border-bottom: 10px solid white; /* 底部边框 */
}
</style>
