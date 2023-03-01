import React, { useState } from "react"
import DatePicker from "./DatePicker";
import "./LoginStyle.css";

//From original Login
import * as yup from "yup";
import {useNavigate} from "react-router-dom";
import {useDispatch} from "react-redux";
import {setLogin} from "../state";

//Learning how to use yup?
const registerSchema = yup.object().shape({
    name: yup.string().required("required"),
    email: yup.string().email("invalid email").required("required"),
    password: yup.string().required("required"),
    birthday: yup.string().required("required")
});

const loginSchema = yup.object().shape({
    email: yup.string().email("invalid email").required("required"),
    password: yup.string().required("required"),
});

// initial values
const initialValuesRegister = {
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    birthday: ""
};

const initialValuesLogin = {
    email: "",
    password: "",
};

export default function (props) {
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const signUp = async (values, onSubmitProps) => {
        // TODO: make API call to create new user in db
        onSubmitProps.resetForm();
    };

    const signIn = async (values, onSubmitProps) => {
        // TODO: make API call to authenticate user
        onSubmitProps.resetForm();
        dispatch(
            setLogin({
                user: values.email
            })
        );
        navigate("/home");
    };

  let [authMode, setAuthMode] = useState("signin")

  const changeAuthMode = () => {
    setAuthMode(authMode === "signin" ? "signup" : "signin")
  }
  
  if (authMode === "signin") {
    return (
      <div className="Form-container">
        <form className="Form">
          <div className="Form-content">
            <h3 className="Form-title">Sign In</h3>
            <div className="form-group mt-3">
              <label>Email address</label>
              <input
                id="user_email"
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
                type="password"
                className="form-control mt-1"
                placeholder="Enter password"
                required
              />
            </div>
            <div className="d-grid gap-2 mt-3">
              <button 
                id="button_signIn"
                type="submit"
                onClick={signIn}>
                Submit
              </button>
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
      <form className="Form">
        <div className="Form-content">
          <h3 className="Form-title">Sign Up</h3>
          <div className="form-group mt-3">
            <label>Full Name</label>
            <input
              id="new_name"
              type="name"
              className="form-control mt-1"
              placeholder="Full Name"
              required
            />
          </div>
          <div className="form-group mt-3">
            <label>Email address</label>
            <input
              id="new_email"
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
              type="password"
              className="form-control mt-1"
              placeholder="Password"
              required
            />
          </div>
          <div className="form-group mt-3">
            <label>Birthday</label>
            <DatePicker id="new_birth"></DatePicker>
          </div>
          <div className="d-grid gap-2 mt-3">
            <button 
              id="button_signup"
              type="submit"
              onClick={signUp}>
              Submit
            </button>
          </div>
          <div className="text-center">
            Have an account?{" "}
            <span className="link" onClick={changeAuthMode}>
              Sign In
            </span>
          </div>
        </div>
      </form>
    </div>
  )
}
