import axios from "axios";

const axiosInstance = axios.create({
    baseURL: "http://localhost:8080/",
    timeout: 5000,
    timeoutErrorMessage: "Request timed out."
});

export default axiosInstance;