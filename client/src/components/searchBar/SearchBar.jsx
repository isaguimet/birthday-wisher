import {useState} from "react";
import axios from "axios";

const SearchBar = (props) => {
    const [input, setInput] = useState("");

    const handleChange = (event) => {
        setInput(event.target.value);
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        axios.get(`http://localhost:8080/users/byEmail/${input}`).then((response) => {
            props.setLoading(false)
            props.setData(response.data)
            props.setError(null)
            props.setStatus(200)
        }).catch((err) => {
            props.setLoading(false);
            props.setStatus(parseInt(err.response.status))
            if (err.response) {
                props.setError(err.response.data);
            } else {
                props.setError(err.message);
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