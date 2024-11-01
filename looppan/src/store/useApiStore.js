import { defineStore } from "pinia";

export const useApiStore = defineStore("api", () => {
  const user = {
    updateUserInfo: "/api/updateUserInfo",
    updatePassword: "/api/updatePassword",
    getUserInfo: "/api/getUserInfo",
    sendEmailCheckCode: "/api/sendEmailCode",
    getPicCheckCode: "/api/getPicCheckCode",
    login: "/api/login",
    emailCheckCode: "/api/sendEmailCode",
    register: "/api/register",
    getAvatarByte: "/api/user/getAvatarByte",
    getUseSpace: "/api/user/getUseSpace",
    forgetPassword: "/api/forgetPassword",
  };

  const file = {
    createFile: "/api/file/createFile",
    getFileList: "/api/file/getFileList",
    uploadFile: "/api/file/uploadFile",
    uploadFile2: "/api/file/uploadFile2",
    uploadFileCancel: "/api/file/uploadFileCancel",
    checkFilename: "/api/file/checkFilename",
    deleteSelectedFiles: "/api/file/deleteSelectedFiles",
    getFolderList: "/api/file/getFolderList",
    moveFiles: "/api/file/moveFiles",
    getAllCategoryFile: "/api/file/getAllCategoryFile",
    renameFile: "/api/file/renameFile",
    returnFileByte: "/api/file/returnFileByte",
    downloadFile: "/api/file/download",
    fileSearch: "/api/file/search",
    //
    shareFile: "/api/file/shareFile",
    getSharedFilesList: "/api/file/getSharedFilesList",
    getSharedFileUrl: "/api/file/getSharedFileUrl",
    cancelSharedFile: "/api/file/cancelSharedFile",
    returnSharedFileByte: "/api/file/returnSharedFileByte",
    getAvatarByte: "/api/getAvatarByte",
    //
    shareCheckCode: "/api/shareCheckCode",
    getSharedUserInfo: "/api/getSharedUserInfo",
    getSharedFileInfo: "/api/shareFileInfo",
    downloadSharedFile: "/api/downloadSharedFile",
    saveMyPan: "/api/saveMyPan",
    //
    getRecycleFiles: "/api/getRecycleFiles",
    deleteRecycleFiles: "/api/deleteRecycleFiles",
    cancelDelete: "/api/cancelDelete",
  };

  return {
    user,
    file,
  };
});
