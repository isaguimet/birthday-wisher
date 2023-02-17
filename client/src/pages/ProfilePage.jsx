import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Theme from "../theme/Theme";
import { StyledDiv, Profile, Board, BoardContainer} from "./ProfilePage.style"
import BirthdayCard from '../components/birthdayCard/BirthdayCard';
import ProfilePic from '../components/profilePic/ProfilePic';
import Icons from '../Icons';


const ProfilePage = () => {

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
                            John Doe 23
                        </Row>
                        <Row>
                            January 1, 1999
                        </Row>
                        </Container>
                    </Col>
                    <Col>Settings</Col>
                </Row>
            </Container>
            </Profile>
            <BoardContainer>
            <Board>
                <BirthdayCard>
                    Happy brithday!
                </BirthdayCard>
                <BirthdayCard>
                    Happy brithday Joe!
                </BirthdayCard>
                <BirthdayCard>
                    Happy brithday!
                </BirthdayCard>
                <BirthdayCard>
                    Happy brithday Joe!
                </BirthdayCard>
            </Board>
            </BoardContainer>
            
            <BoardContainer>
                <Board>
                <BirthdayCard>
                    Happy brithday!
                </BirthdayCard>
                <BirthdayCard>
                    Happy brithday Joe!
                </BirthdayCard>
                <BirthdayCard>
                    Happy brithday!
                </BirthdayCard>
                <BirthdayCard>
                    Happy brithday Joe!
                </BirthdayCard>
                </Board>
            </BoardContainer>
        </StyledDiv>
        </Theme>
    );
};

export default ProfilePage;