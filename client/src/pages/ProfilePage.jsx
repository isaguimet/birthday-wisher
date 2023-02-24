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


const ProfilePage = (props) => {

    const [loading, setLoading] = useState(false);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    // TODO: once user ID is stored in redux, useSelector instead of this hard-coded obj.
    // const user = useSelector(state => state.user);
    const user = {id: "63f300a9aa937b2f68a15e23"};

    useEffect(() => {
        // TODO: once we have user ID stored in redux, load it here as user.id to use in request.
        setLoading(true);
        axios.get(`http://localhost:8080/boards/byUserId/${user.id}`).then((response) => {
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