import {useState} from "react";
import {Button, TextField, Typography} from "@mui/material";
import {Formik} from "formik";
import * as yup from "yup";
import {useNavigate} from "react-router-dom";
import {useDispatch} from "react-redux";
import {setLogin} from "../store/user";

// yup validation schemas
const registerSchema = yup.object().shape({
    firstName: yup.string().required("required"),
    lastName: yup.string().required("required"),
    email: yup.string().email("invalid email").required("required"),
    password: yup.string().required("required"),
    // TODO: add birthday
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
    // TODO: add birthday
};

const initialValuesLogin = {
    email: "",
    password: "",
};

const LoginForm = () => {

    const dispatch = useDispatch();
    const navigate = useNavigate();

    const [formType, setFormType] = useState("login");
    const isLogin = formType === "login";
    const isRegister = formType === "register";

    const register = async (values, onSubmitProps) => {
        // TODO: make API call to create new user in db
        onSubmitProps.resetForm();
        setFormType("login");
    };

    const login = async (values, onSubmitProps) => {
        // TODO: make API call to authenticate user
        onSubmitProps.resetForm();
        dispatch(
            setLogin({
                user: values.email
            })
        );
        navigate("/home");
    };

    const handleFormSubmit = async (values, onSubmitProps) => {
        if (isLogin) await login(values, onSubmitProps);
        if (isRegister) await register(values, onSubmitProps);
    };

    return (
        <Formik
            onSubmit={handleFormSubmit}
            initialValues={isLogin ? initialValuesLogin : initialValuesRegister}
            validationSchema={isLogin ? loginSchema : registerSchema}
        >
            {({
                  values,
                  errors,
                  touched,
                  handleBlur,
                  handleChange,
                  handleSubmit,
                  setFieldValue,
                  resetForm
              }) => (
                <form onSubmit={handleSubmit}>
                    <div>
                        {isRegister && (
                            <>
                                <TextField
                                    label={"First Name"}
                                    onBlur={handleBlur}
                                    onChange={handleChange}
                                    value={values.firstName}
                                    name={"firstName"}
                                    error={Boolean(touched.firstName) && Boolean(errors.firstName)}
                                    helperText={touched.firstName && errors.firstName}
                                />
                                <TextField
                                    label={"Last Name"}
                                    onBlur={handleBlur}
                                    onChange={handleChange}
                                    value={values.lastName}
                                    name={"lastName"}
                                    error={Boolean(touched.lastName) && Boolean(errors.lastName)}
                                    helperText={touched.lastName && errors.lastName}
                                />
                            </>
                        )}

                        {/* Section common to both login & register */}
                        <TextField
                            label={"Email"}
                            onBlur={handleBlur}
                            onChange={handleChange}
                            value={values.email}
                            name={"email"}
                            error={Boolean(touched.email) && Boolean(errors.email)}
                            helperText={touched.email && errors.email}
                        />
                        <TextField
                            label={"Password"}
                            type={"password"}
                            onBlur={handleBlur}
                            onChange={handleChange}
                            value={values.password}
                            name={"password"}
                            error={Boolean(touched.password) && Boolean(errors.password)}
                            helperText={touched.password && errors.password}
                        />
                    </div>

                    {/* BUTTONS */}
                    <div>
                        <Button
                            fullWidth
                            type={"submit"}
                        >
                            {isLogin ? "LOGIN" : "REGISTER"}
                        </Button>
                        <Typography
                            onClick={() => {
                                setFormType(isLogin ? "register" : "login");
                                resetForm();
                            }}
                            sx={{
                                textDecoration: "underline",
                                "&:hover": {
                                    cursor: "pointer",
                                },
                            }}
                        >
                            {isLogin
                                ? "Don't have an account? Sign up here."
                                : "Already have an account? Login here."}
                        </Typography>
                    </div>
                </form>
            )}
        </Formik>
    );
};

export default LoginForm;