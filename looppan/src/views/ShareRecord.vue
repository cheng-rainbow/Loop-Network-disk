<template>
  <div class="title">
    <button @click="cancelSharedFiles" type="button" :disabled="!selectedCheck.length" :class="['btn', 'btn-share']">
      <i class="bi bi-ban"></i>
      取消分享
    </button>
    <div v-if="filesCache == null || filesCache.length == 0" class="path1">分享文件</div>
    <div v-else class="path2">
      <span @click="returnLastFolder" class="return-last">返回上一级</span>
      <span class="path2-share-file">分享文件</span>
      <div class="path2-filename" v-for="file in filesCache" :key="file.shareId">{{ file.fileName }} ></div>
    </div>
  </div>
  <div class="content">
    <div class="container">
      <div class="row myrow my-title">
        <div class="col-auto">
          <input v-model="isChecked" @change="selectAll($event.target.checked)" class="form-check-input" type="checkbox" value="" id="defaultCheck1" />
        </div>
        <div class="col-6 container-title">文件名</div>
        <div class="col-2 container-title">分享时间</div>
        <div class="col-2 container-title">失效时间</div>
        <div class="col-auto container-title">浏览次数</div>
      </div>

      <div v-for="file in files" :key="file.fileId" tabindex="0" class="row myrow">
        <div class="col-auto">
          <input :checked="isFileSelected(file.shareId)" @change="selectedCheckBox(file.shareId)" class="form-check-input" type="checkbox" id="defaultCheck1" />
        </div>
        <div class="col-4 my-fileName">
          <i v-if="file.fileCategory == statickey.category.folder" class="bi bi-folder2 my-floder my-floder-folder"></i>
          <i v-else-if="file.fileCategory == statickey.category.video" class="bi bi-file-earmark-play my-floder"></i>
          <i v-else-if="file.fileCategory == statickey.category.audio" class="bi bi-file-music my-floder"></i>
          <!-- <i v-else-if="file.fileCategory == statickey.category.image" class="bi bi-images my-floder"></i> -->
          <img v-else-if="file.fileCategory == statickey.category.image" class="my-floder my-image" :src="file.fileCover" alt="" />

          <i v-else-if="file.fileCategory == statickey.category.document" class="bi bi-file-word my-floder"></i>
          <i v-else-if="file.fileCategory == statickey.category.other" class="bi bi-file-earmark-medical my-floder"></i>
          <span v-if="file.fileCategory == statickey.category.folder || file.fileCategory == statickey.category.document" class="file-name folder-name">{{ file.fileName }}</span>
          <span v-else class="file-name" @click="clickFileName(file)">{{ file.fileName }}</span>
        </div>
        <div class="col-2 my-icon">
          <div @click="getSharedFileUrl(file)" class="copy-link">
            <i class="bi bi-link-45deg"></i>
            <span>复制链接</span>
          </div>
          <div @click="cancelSharedFile(file)" class="cancle-link">
            <i class="bi bi-ban"></i>
            <span>取消分享</span>
          </div>
        </div>
        <div class="col-2">
          <span>{{ getShareTime(file.shareTime) }}</span>
        </div>
        <div class="col-2">
          <span>{{ getFailTime(file.shareTime, file.failTime) }}</span>
        </div>
        <div class="col-auto">
          <span>{{ file.views }}</span>
        </div>
      </div>
    </div>
    <div v-if="alertStore.load.isLoading == false && files.length == 0" class="noll">
      <span>暂无数据</span>
    </div>
  </div>

  <!-- image preview modal -->
  <div v-if="isPreviewVisibleImage" class="preview-modal">
    <div @click="closePreviewImage" class="close-icon">
      <i class="bi bi-x"></i>
    </div>
    <div class="preview-content">
      <img class="isImageScaled" :src="imageUrl" alt="Image Preview" />
    </div>
  </div>

  <!-- viedo preview modal -->
  <div v-if="isPreviewVisibleVideo" class="preview-modal">
    <div @click="closePreviewVideo" class="close-icon">
      <i class="bi bi-x"></i>
    </div>
    <div class="preview-content">
      <video class="isVideoScaled" :src="videoUrl" controls></video>
    </div>
  </div>

  <!-- 音频预览模态框 -->
  <div v-if="isPreviewVisibleAudio" class="preview-modal">
    <div @click="closePreviewAudio" class="close-icon">
      <i class="bi bi-x"></i>
    </div>
    <div class="preview-content">
      <audio class="isAudioScaled" :src="audioUrl" controls></audio>
    </div>
  </div>

  <LoadingBox />
</template>

<script setup>
import { nextTick, onMounted, ref, watch } from "vue";
import axios from "@/utils/axiosInstance";
import { useApiStore } from "@/store/useApiStore";
import statickey from "@/utils/statickey";
import LoadingBox from "@/components/LoadingBox.vue";
import { useAlertStore } from "@/store/useAlertStore";
import { useRoute } from "vue-router";
import router from "@/router";
import { Alert } from "bootstrap";

const apiStore = useApiStore();
const alertStore = useAlertStore();

const files = ref([]);
let selectedCheck = ref([]);
const route = useRoute();

const filesCache = ref([]);
watch(
  () => route.query,
  async (newPath, oldPath) => {
    await nextTick();
    getSharedFilesList();
    if (newPath.path == null || (oldPath.path != null && newPath.path.length < oldPath.path.length)) {
      filesCache.value.pop();
    }
  }
);

const returnLastFolder = () => {
  let path = route.query.path;
  path = path.substring(0, path.lastIndexOf("/"));
  if (path != "" && path != null) {
    router.push({ name: route.name, query: { path: path } });
  } else {
    router.push({ name: route.name });
  }
};

onMounted(() => {
  getSharedFilesList();
});

const getSharedFilesList = async () => {
  alertStore.load.isLoading = true;
  try {
    let resp;
    if (route.query.path == null) {
      resp = await axios.post(apiStore.file.getSharedFilesList, {}).then((resp) => {
        files.value = resp.data;
      });
    } else {
      resp = await axios
        .post(apiStore.file.getSharedFilesList, {
          shareId: route.query.path,
        })
        .then((resp) => {
          files.value = resp.data;
        });
    }
    const promises = files.value.map(async (file) => {
      if (file.fileCategory == statickey.category.image) {
        file.fileCover = await getImageUrl(file.fileId);
      }
      return file;
    });
    files.value = await Promise.all(promises);
  } finally {
    alertStore.load.isLoading = false;
  }
};

const isPreviewVisibleVideo = ref(false);
const isPreviewVisibleImage = ref(false);
const isPreviewVisibleAudio = ref(false);

let imageUrl = ref("");
let videoUrl = ref("");
let audioUrl = ref("");

const closePreviewImage = () => {
  isPreviewVisibleImage.value = false;
};

const closePreviewVideo = () => {
  isPreviewVisibleVideo.value = false;
};

const closePreviewAudio = () => {
  isPreviewVisibleAudio.value = false;
  selectedDuration.value = null;
};

const clickFileName = (file) => {
  if (file.fileCategory == statickey.category.folder || file.fileCategory == statickey.category.document) {
    return;
  }

  axios
    .get(
      apiStore.file.returnSharedFileByte,
      {
        shareId: file.shareId,
      },
      "blob"
    )
    .then((resp) => {
      const Blob = resp; // 获取 Blob 数据
      if (file.fileCategory == statickey.category.video) {
        isPreviewVisibleVideo.value = true;
        videoUrl.value = URL.createObjectURL(Blob);
      } else if (file.fileCategory == statickey.category.audio) {
        isPreviewVisibleAudio.value = true;
        audioUrl.value = URL.createObjectURL(Blob);
      } else if (file.fileCategory == statickey.category.image) {
        isPreviewVisibleImage.value = true;
        imageUrl.value = URL.createObjectURL(Blob);
      }
    });
};

const getShareTime = (time) => {
  const date = new Date(time);

  const options = {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false, // 使用24小时制
  };
  const formattedTime = date.toLocaleString("zh-CN", options); // 根据需要选择语言

  return formattedTime;
};

const getFailTime = (shareTime, failTime) => {
  if (shareTime == failTime) {
    return "永久";
  }

  const date = new Date(failTime);

  const options = {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false, // 使用24小时制
  };
  const formattedTime = date.toLocaleString("zh-CN", options); // 根据需要选择语言

  return formattedTime;
};
const isChecked = ref(false);
const selectAll = (isSelected) => {
  if (isSelected) {
    selectedCheck.value = [];
    for (let i = 0; i < files.value.length; i++) {
      selectedCheck.value.unshift(files.value[i].shareId); // 使用 files.value
    }
  } else {
    selectedCheck.value = [];
  }
};

const selectedCheckBox = (shareId) => {
  if (selectedCheck.value.includes(shareId)) {
    selectedCheck.value = selectedCheck.value.filter((id) => id != shareId);
  } else {
    selectedCheck.value.unshift(shareId);
  }
};

const isFileSelected = (shareId) => {
  return selectedCheck.value.includes(shareId);
};

const getImageUrl = async (fileId) => {
  const resp = await axios.post(
    apiStore.file.returnFileByte,
    {
      fileId: fileId,
    },
    "blob"
  );
  const url = URL.createObjectURL(resp);
  return url;
};

// const getSharedFileUrl = (file) => {
//   axios
//     .post(apiStore.file.getSharedFileUrl, {
//       shareId: file.shareId,
//     })
//     .then((resp) => {
//       // 将 URL 复制到剪贴板
//       navigator.clipboard.writeText(resp.data);
//     });
// };

const getSharedFileUrl = (file) => {
  axios
    .post(apiStore.file.getSharedFileUrl, {
      shareId: file.shareId,
    })
    .then((resp) => {
      const url = resp.data;
      if (navigator.clipboard) {
        // 使用 Clipboard API
        navigator.clipboard.writeText(url).catch((err) => {});
      } else {
        // 备选方案：使用 document.execCommand
        const textarea = document.createElement("textarea");
        textarea.value = url;
        document.body.appendChild(textarea);
        textarea.select();
        try {
          document.execCommand("copy");
        } catch (err) {
          Alert("浏览器不支持, 请手动复制" + url);
        } finally {
          document.body.removeChild(textarea);
        }
      }
    })
    .catch((error) => {
      console.error("Error getting shared file URL:", error);
    });
};

const cancelSharedFiles = () => {
  axios.post(apiStore.file.cancelSharedFile, selectedCheck.value).then((resp) => {
    files.value = files.value.filter((file) => !selectedCheck.value.includes(file.shareId));
    selectedCheck.value = [];
    isChecked.value = false;
  });
};

const cancelSharedFile = (file) => {
  axios.post(apiStore.file.cancelSharedFile, [file.shareId]).then((resp) => {
    deleteFileByShareId(file.shareId);
  });
};

const deleteFileByShareId = (shareId) => {
  files.value = files.value.filter((file) => file.shareId !== shareId);
};
</script>

<style lang="scss" scoped>
.folder-name {
  cursor: default !important;
}

.file-name:hover {
  cursor: pointer;
  color: #5faeff;
}
.file-name {
  color: #636d7d;
  text-decoration: none;
}

.my-image {
  width: 24px;
  height: 24px;
  border-radius: 8px;
}

.isImageScaled {
  transform: scale(0.7); /* 进行缩放 */
  transition: transform 0.1s; /* 添加过渡效果 */
}

.isVideoScaled {
  transform: scale(0.9); /* 进行缩放 */
  transition: transform 0.1s;
}

.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  .close-icon {
    position: absolute;
    top: 10px;
    right: 20px;
    font-size: 30px;
    cursor: pointer;
    color: #fff;
  }
  .preview-content {
    max-width: 90%;
    max-height: 90%;
    margin-bottom: 1%;
    display: flex; /* 使内容为flex布局 */
    align-items: center; /* 垂直居中 */
    justify-content: center; /* 水平居中 */
    img {
      max-width: 100%;
      max-height: 100%;
      border-radius: 10px;
      object-fit: cover; /* 保持图像的宽高比 */
    }
    video {
      max-width: 100%;
      max-height: 100%;
      border-radius: 10px;
      object-fit: cover; /* 保持图像的宽高比 */
    }
  }
}

.active {
}
.not-active {
  opacity: 0.5 !important;
}

span {
  user-select: none;
}

.noll {
  height: calc(100% - 60px);
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
  span {
    width: 100px;
    height: 50px;
    background-color: #edf3f8;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    font-size: 14px;
  }
}

.my-floder-folder {
  color: #ffcf40;
}

.my-floder {
  font-size: 24px;
  margin-right: 8px;
}

.my-fileName {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.my-fileName i {
  font-size: 24px;
  margin-right: 8px;
}

.my-fileName span {
  cursor: pointer;
}

.my-fileName span:hover {
  color: #3f9eff;
}

.my-icon {
  opacity: 0;
  display: flex;
  color: #3f9eff;
  .copy-link {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    margin-right: 5px;
    span {
      margin: 0 3px;
    }
    i {
      font-size: 20px;
    }
  }
  .cancle-link {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    span {
      margin: 0 3px;
    }
  }
}

.myrow:hover {
  background-color: #f4f7fa;

  .my-icon {
    opacity: 1;
  }
}

.myrow:focus {
  background-color: #ecf5ff;
}

.title {
  margin-top: 20px;
  width: 100%;
  height: 85px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  button {
    width: 120px;
  }
  .path1 {
    font-weight: 700;
  }
  .path2 {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    .return-last {
      color: #5faeff;
      padding-right: 8px;
      border-right: 1px solid rgba(0, 0, 0, 0.08);
      cursor: pointer;
    }
    .path2-share-file {
      color: #5faeff;
      padding: 0 8px;
    }
    .path2-filename {
      font-weight: 800;
      color: #5faeff;
    }
  }
}

.content {
  height: calc(100% - 60px);
  width: 100%;
  overflow-y: auto;
  position: relative;
  white-space: nowrap;
  .container {
    position: absolute;
    left: 0;
    .myrow {
      height: 50px;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      min-width: 900px;
      border-bottom: solid 1px rgba(0, 0, 0, 0.08);
    }
    .container-title {
      font-size: 14px;
      font-weight: 700;
    }
  }
}

.my-title {
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 10;
}

// button
.title {
  .btn-share {
    background-color: #3f9eff;
  }
  .btn-share:hover {
    background-color: #79bbff;
  }
  .btn-share:active {
    background-color: #347ecc;
  }
}
</style>
