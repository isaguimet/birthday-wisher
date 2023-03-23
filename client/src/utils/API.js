import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "",
    timeout: 5000,
    timeoutErrorMessage: "Request timed out."
});

export default axiosInstance;