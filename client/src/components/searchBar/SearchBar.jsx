import {useState} from "react";
import axiosInstance from "../../utils/API";

const SearchBar = (props) => {
    const [input, setInput] = useState("");

    const handleChange = (event) => {
        setInput(event.target.value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        props.setLoading(true);
        axiosInstance.get(`https://proxy1-08e6.onrender.com/users/byEmail/${input}`).then((response) => {
            props.setLoading(false)
            props.setData(response.data)
            props.setError(null)
        }).catch((err8080) => {
            if (err8080.response) {
                props.setLoading(false);
                props.setError(err8080.response.data);
            } else {
                axiosInstance.get(`https://proxy2-no3f.onrender.com/users/byEmail/${input}`).then((response) => {
                    props.setLoading(false)
                    props.setData(response.data)
                    props.setError(null)
                }).catch((err) => {
                    props.setLoading(false);
                    if (err.response) {
                        props.setError(err.response.data);
                    } else {
                        props.setError(err.message);
                    }
                });
            }
        });
    }

    return (
        <>
            <form onSubmit={handleSubmit}>
                <input type={"search"} placeholder={"Enter friend email here..."} value={input} onChange={handleChange}/>
                <button type={"submit"}>Find Friend</button>
            </form>
        </>
    );
}

export default SearchBar;