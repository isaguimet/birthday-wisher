import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import ProfilePic from "../profilePic/ProfilePic";
import Icons from "../../Icons";
import {Age, Profile, ProfileInfo} from "../../pages/ProfilePage.style";
import {useEffect, useState} from "react";
import {Alert} from "reactstrap";
import axiosInstance from "../../utils/API";

/**
 * A component for fetching and rendering personal information about a specific user.
 * @param props {object} Expected props for this component:
 * - profileUser {string} the unique identifier for the user this component should display info about.
 * @returns {JSX.Element}
 * @constructor
 */
const ProfileSection = (props) => {

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true);
        axiosInstance.get(`https://proxy1-08e6.onrender.com/users/${props.profileUser}`).then((response) => {
            setLoading(false);
            setData(response.data);
            setError(null);
        }).catch((err8080) => {
            if (err8080.response) {
                setLoading(false);
                setError(err8080.response.data);
            } else {
                axiosInstance.get(`https://proxy2-no3f.onrender.com/users/${props.profileUser}`).then((response) => {
                    setLoading(false);
                    setData(response.data);
                    setError(null);
                }).catch((err) => {
                    setLoading(false);
                    if (err.response) {
                        setError(err.response.data);
                    } else {
                        setError(err.message);
                    }
                });
            }
        });
    }, []);

    const getBirthday = () => {
        let bday = new Date(data.birthdate);
        let today = new Date();

        let diff = today.getTime() - bday.getTime();
        let age = Math.floor(diff / (1000 * 3600 * 24 * 365))
        return (
            <>{age}</>
        );
    };

    const handleAlertToggle = () => {
        setError(null);
    };

    return (
        <Profile>
            <Container>
                {!!error && (
                    <Alert color={"danger"} toggle={handleAlertToggle}>
                        Error fetching user info: {error}
                    </Alert>
                )}
                <Row>
                    {loading && <div>Loading user info...</div>}
                    {!loading && data ? (
                        <>
                            {/*TODO: if we are storing user profile pic in db, should access from data.profilePic*/}
                            <Col style={{display: "flex", justifyContent: "center"}}><ProfilePic src={Icons[3]}/></Col>
                            <Col xs={"auto"}>
                                <Container>
                                    <Row>
                                        <ProfileInfo>
                                            <h1>{data.firstName + " " + data.lastName}</h1>
                                        </ProfileInfo>
                                    </Row>
                                    <Row>
                                        <h4>
                                            <svg xmlns="http://www.w3.org/2000/svg"
                                                 className="icon icon-tabler icon-tabler-cake" width="27" height="27"
                                                 viewBox="0 0 24 24" strokeWidth="2" stroke="currentColor" fill="none"
                                                 strokeLinecap="round" strokeLinejoin="round">
                                                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                                <path d="M3 20h18v-8a3 3 0 0 0 -3 -3h-12a3 3 0 0 0 -3 3v8z"></path>
                                                <path
                                                    d="M3 14.803c.312 .135 .654 .204 1 .197a2.4 2.4 0 0 0 2 -1a2.4 2.4 0 0 1 2 -1a2.4 2.4 0 0 1 2 1a2.4 2.4 0 0 0 2 1a2.4 2.4 0 0 0 2 -1a2.4 2.4 0 0 1 2 -1a2.4 2.4 0 0 1 2 1a2.4 2.4 0 0 0 2 1c.35 .007 .692 -.062 1 -.197"></path>
                                                <path d="M12 4l1.465 1.638a2 2 0 1 1 -3.015 .099l1.55 -1.737z"></path>
                                            </svg>
                                            {data.birthdate}
                                        </h4>

                                    </Row>
                                </Container>
                            </Col>
                            <Col>
                                <h1 style={{display: "inline"}}><Age>{getBirthday()}</Age></h1>
                            </Col>
                            <Col>
                                <a href="/account">
                                    Settings
                                    <svg xmlns="http://www.w3.org/2000/svg" className="icon icon-tabler icon-tabler-settings"
                                         width="30" height="30" viewBox="0 0 24 24" strokeWidth="1.5" stroke="#2c3e50"
                                         fill="none" strokeLinecap="round" strokeLinejoin="round">
                                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                        <path
                                            d="M10.325 4.317c.426 -1.756 2.924 -1.756 3.35 0a1.724 1.724 0 0 0 2.573 1.066c1.543 -.94 3.31 .826 2.37 2.37a1.724 1.724 0 0 0 1.065 2.572c1.756 .426 1.756 2.924 0 3.35a1.724 1.724 0 0 0 -1.066 2.573c.94 1.543 -.826 3.31 -2.37 2.37a1.724 1.724 0 0 0 -2.572 1.065c-.426 1.756 -2.924 1.756 -3.35 0a1.724 1.724 0 0 0 -2.573 -1.066c-1.543 .94 -3.31 -.826 -2.37 -2.37a1.724 1.724 0 0 0 -1.065 -2.572c-1.756 -.426 -1.756 -2.924 0 -3.35a1.724 1.724 0 0 0 1.066 -2.573c-.94 -1.543 .826 -3.31 2.37 -2.37c1 .608 2.296 .07 2.572 -1.065z"/>
                                        <circle cx="12" cy="12" r="3"/>
                                    </svg>
                                </a>
                            </Col>
                        </>
                    ) : null}
                </Row>
            </Container>
        </Profile>
    );
};

export default ProfileSection;