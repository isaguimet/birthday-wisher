import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Theme from "../theme/Theme";
import {StyledDiv, Profile} from "./ProfilePage.style"
import ProfilePic from '../components/profilePic/ProfilePic';
import Icons from '../Icons';
import {useLocation} from "react-router-dom";
import BoardSection from "../components/boardSection/BoardSection";


const ProfilePage = (props) => {
    // the ID of the user whose profile is shown on this page
    const path = useLocation().pathname;
    const profileUser = path.substring(path.lastIndexOf("/") + 1);

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
                <BoardSection profileUser={profileUser}/>
            </StyledDiv>
        </Theme>
    );
};

export default ProfilePage;