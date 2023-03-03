import {useState} from "react"
import DatePicker from "./DatePicker";
import "./LoginStyle.css";
import {useNavigate} from "react-router-dom";
import {useDispatch} from "react-redux";
import {setLogin} from "../store/user";
import axios from "axios";
import { Button } from "@mui/material";

export default function (props) {
    const [date, setDate] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const signUp = (event) => {
        // prevent from immediately refreshing page
        event.preventDefault();

        // make API call to create new user in db
        const data = {
            firstName: event.target.first_name.value,
            lastName: event.target.last_name.value,
            email: event.target.new_email.value,
            password: event.target.new_pass.value,
            birthdate: date
        };
        axios.post(`http://localhost:8080/users/signUp`, data).then((response) => {
            alert("Success! Try logging in with these credentials now.");
        }).catch((err) => {
            let error = "";
            if (err.response) {
                error = err.response.data;
            } else {
                error = err.message;
            }
            alert("Sign Up Error: " + error);
        });
    };

    const signIn = (event) => {
        // prevent from immediately refreshing page
        event.preventDefault();

        // make API call to authenticate user
        const data = {
            email: event.target.user_email.value,
            password: event.target.user_pass.value
        };
        axios.post(`http://localhost:8080/users/login`, data).then((response) => {
            dispatch(
                setLogin({
                    user: response.data
                })
            );
            navigate("/home");
        }).catch((err) => {
            let error = "";
            if (err.response) {
                error = err.response.data;
            } else {
                error = err.message;
            }
            alert("Sign In Error: " + error);
        });
    };

    let [authMode, setAuthMode] = useState("signin")

    const changeAuthMode = () => {
        setAuthMode(authMode === "signin" ? "signup" : "signin")
    }

    if (authMode === "signin") {
        return (
            <div className="Form-container">
                <form className="Form" onSubmit={signIn}>
                    <div className="Form-content">
                        <h3 className="Form-title">Sign In</h3>
                        <div className="form-group mt-3">
                            <label>Email address</label>
                            <input
                                id="user_email"
                                name="user_email"
                                type="email"
                                className="form-control mt-1"
                                placeholder="Enter email"
                                required
                            />
                        </div>
                        <div className="form-group mt-3">
                            <label>Password</label>
                            <input
                                id="user_pass"
                                name="user_pass"
                                type="password"
                                className="form-control mt-1"
                                placeholder="Enter password"
                                required
                            />
                        </div>
                        <div className="d-grid gap-2 mt-3">
                            <Button
                                // id="button_signIn"
                                variant="contained"
                                type={"submit"}
                            >
                                Submit
                            </Button>
                            <div className="text-center">
              <span className="link" onClick={changeAuthMode}>
                Create an Account
              </span>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        )
    }
    return (
        <div className="Form-container">
            <form className="Form" onSubmit={signUp}>
                <div className="Form-content">
                    <h3 className="Form-title">Sign Up</h3>
                    <div className="form-group mt-3">
                        <label>First Name</label>
                        <input
                            id="first_name"
                            name="first_name"
                            type="name"
                            className="form-control mt-1"
                            placeholder="First Name"
                            required
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Last Name</label>
                        <input
                            id="last_name"
                            name="last_name"
                            type="name"
                            className="form-control mt-1"
                            placeholder="Last Name"
                            required
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Email address</label>
                        <input
                            id="new_email"
                            name="new_email"
                            type="email"
                            className="form-control mt-1"
                            placeholder="Email Address"
                            required
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Password</label>
                        <input
                            id="new_pass"
                            name="new_pass"
                            type="password"
                            className="form-control mt-1"
                            placeholder="Password"
                            required
                        />
                    </div>
                    <div className="form-group mt-3">
                        <label>Birthday</label>
                        <DatePicker id="new_birth" setDate={setDate}></DatePicker>
                    </div>
                    <div className="d-grid gap-2 mt-3">
                        <button
                            id="button_signup"
                            type="submit"
                        >
                            Submit
                        </button>
                    </div>
                    <div className="text-center">
                        Have an account?{" "}
                        <span className="link" onClick={changeAuthMode}>Sign In</span>
                    </div>
                </div>
            </form>
        </div>
    )
}
