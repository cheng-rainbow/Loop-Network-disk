<template>
  <div>
    <!-- 模态框 -->
    <div class="modal fade" :class="{ show: isVisible }" :style="{ display: isVisible ? 'block' : 'none' }" tabindex="-1" aria-hidden="!isVisible">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">填写信息</h5>
            <button type="button" class="btn-close" @click="closeModal"></button>
          </div>
          <div class="modal-body">
            <div class="my-form">
              <div class="my-username">
                <label for="password" class="form-label">新密码</label>
                <input type="password" class="form-control" v-model="password" required />
                <label for="password" class="form-label">确认密码</label>
                <input type="password" class="form-control" v-model="confirmPassword" required />
                <label for="checkCode" class="form-label">验证码</label>
                <div class="checkcode-css">
                  <input class="form-control" id="checkCode" v-model="emailCheckCode" />
                  <button class="btn btn-primary" :disabled="isSending" @click="sendEmailCheckCode2">{{ isSending ? "发送中..." : "发送验证码" }}</button>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="button" class="btn btn-primary" @click="handleSubmit">确认</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 模态框背景遮罩 -->
    <div v-if="isVisible" class="modal-backdrop fade show"></div>
  </div>
</template>

<script setup>
import { useApiStore } from "@/store/useApiStore";
import { useUserStore } from "@/store/useUserStore";
import axios from "@/utils/axiosInstance";
import { onMounted, ref } from "vue";

const userStore = useUserStore();
const apiStore = useApiStore();

const isVisible = ref(false);

const password = ref(userStore.user.password);
const confirmPassword = ref("");
const emailCheckCode = ref("");
const isSending = ref(false);

onMounted(() => {
  password.value = "";
  confirmPassword.value = "";
  emailCheckCode.value = "";
});

const showModal = () => {
  password.value = "";
  confirmPassword.value = "";
  emailCheckCode.value = "";
  isVisible.value = true;
};

const closeModal = () => {
  isVisible.value = false;
};

const handleSubmit = async () => {
  if (!userStore.user.email.includes("@")) {
    alert("admin账号 不支持修改密码");
    return;
  }
  const resp = await axios.post(apiStore.user.updatePassword, {
    email: userStore.user.email,
    password: password.value,
    confirmPassword: confirmPassword.value,
    emailCheckCode: emailCheckCode.value,
  });
  isVisible.value = false;
  userStore.user.token = "";
};

const sendEmailCheckCode2 = async () => {
  isSending.value = true;
  if (!userStore.user.email.includes("@")) {
    alert("admin账号 不支持发送验证码");
    isSending.value = false;
    return;
  }
  try {
    const resp = await axios.post(apiStore.user.sendEmailCheckCode, {
      email: userStore.user.email,
    });
  } finally {
    isSending.value = false;
  }
};

defineExpose({
  showModal,
});
</script>

<style lang="scss" scoped>
.modal-dialog {
  margin-top: 15vh; /* 将模态框向下移动到垂直高度的三分之一处 */
}

.modal {
  display: none; /* 确保模态框在未显示时不占用空间 */
}

.my-password-css {
  margin-top: 10px;
  border-top: solid 1px;
}

.checkcode-css {
  display: flex;
  align-items: center;
  justify-content: space-between;
  button {
    box-sizing: border-box;
    width: 150px;
    margin-left: 20px;
  }
}
</style>
