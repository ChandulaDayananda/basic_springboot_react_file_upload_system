import axios from "axios";

const API_URL = "http://localhost:8081";

export const uploadFiles = (files) => {
    const formData = new FormData();
    files.forEach((file) => {
        formData.append("files", file);
    });

    return axios.post(`${API_URL}/uploadMultiple`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
};


export const getFiles = () => axios.get(`${API_URL}/files`);
