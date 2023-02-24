import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Theme from "../theme/Theme";
import {StyledDiv, Profile} from "./ProfilePage.style"
import ProfilePic from '../components/profilePic/ProfilePic';
import Icons from '../Icons';
import {useEffect, useState} from 'react';
// import {useSelector} from "react-redux";
import BirthdayBoard from "../components/birthdayBoard/BirthdayBoard";
import axios from "axios";
import {useLocation} from "react-router-dom";
import {useSelector} from "react-redux";


const ProfilePage = (props) => {

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    // the ID of the user that is accessing this page
    const loggedInUser = useSelector((state) => state.user.id);

    // the ID of the user whose profile is shown on this page
    const path = useLocation().pathname;
    const profileUser = path.substring(path.lastIndexOf("/") + 1);

    console.log(`user ${loggedInUser} is accessing the profile of user ${profileUser}`);

    useEffect(() => {
        setLoading(true);
        axios.get(`http://localhost:8080/boards/byUserId/${loggedInUser}`).then((response) => {
            setLoading(false);
            setData(response.data);
            setError(null);
        }).catch((err) => {
            setLoading(false);
            setData(null);
            setError(err.response.statusText);
        });
    }, []);

    return (
        <Theme>
            <StyledDiv>
                <Profile>

                    <Container>
                        <Row>
                            <Col><ProfilePic src={Icons[4]}/></Col>
                            <Col>
                                <Container>
                                    <Row>
                                        <h1>John Doe 23</h1>
                                    </Row>
                                    <Row>
                                        <h4>January 1, 1999</h4>
                                    </Row>
                                </Container>
                            </Col>
                            <Col>
                                <h5>Settings</h5>
                            </Col>
                        </Row>
                    </Container>
                </Profile>
                {loading && <div>Loading...</div>}
                {!loading && error ? <div>Error: {error}</div> : null}
                {!loading && data ? (
                    <div>
                        {data.map((board) => (
                            <BirthdayBoard
                                key={board.id}
                                id={board.id}
                                boardId={board.id}
                                year={board.year}
                                open={board.open}
                                public={board.public}
                                messages={board.messages}
                                setLoading={setLoading}
                                setData={setData}
                                setError={setError}
                            />
                        ))}
                    </div>
                ) : null}
            </StyledDiv>
        </Theme>
    );
};

export default ProfilePage;