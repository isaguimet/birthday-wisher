import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Theme from "../theme/Theme";
import { StyledDiv, Profile, Board, BoardContainer} from "./ProfilePage.style"
import BirthdayCard from '../components/birthdayCard/BirthdayCard';
import ProfilePic from '../components/profilePic/ProfilePic';
import Icons from '../Icons';
import ListWishes from '../components/listWishes/ListWishes';
import { useState } from 'react';


const ProfilePage = (props) => {

    const [wishes, setWishes] = useState([]);
    const [wishMessage, setWishMessage] = useState('');
    const [updated, setUpdated] = useState(wishMessage); 

    const handleChange = (event) => {
        setWishMessage(event.target.value);
      };
    
    const handleClick = () => {
        setUpdated(wishMessage);
    };
    

    const addWish = () => {
        setWishes([...wishes, updated])
    }

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
            <BoardContainer>
                2022
                <button onClick={addWish}>Add Wish</button>
                <input
                    type="text"
                    id="wish"
                    name="wish"
                    onChange={handleChange}
                    value={wishMessage}
                />
                <button onClick={handleClick}>Submit Wish</button>
                       
                {/* if no item it doesn't render anything, if no item it should just render blank */}
            <Board>
            {wishes.length ? wishes.map((item, i) => (<ListWishes wish={item} />)) : "no wishes"}
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
                2021
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