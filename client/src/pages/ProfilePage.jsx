import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Theme from "../theme/Theme";
import {StyledDiv, Profile, Board, BoardContainer} from "./ProfilePage.style"
import BirthdayCard from '../components/birthdayCard/BirthdayCard';
import ProfilePic from '../components/profilePic/ProfilePic';
import Icons from '../Icons';
import ListWishes from '../components/listWishes/ListWishes';
import {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {getBoards} from "../store/board";


const ProfilePage = (props) => {

    // const [data, setData] = useState(props.data);
    // const [error, setError] = useState(props.error);
    // const [loading, setLoading] = useState(props.loading);

    const dispatch = useDispatch();
    const board = useSelector(state => state.board)

    useEffect(() => {
        dispatch(getBoards())
    }, []);

    const [wishes, setWishes] = useState([])
    const addWish = () => {
        setWishes([...wishes, "wish"])
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


                    {/* if no item it doesn't render anything, if no item it should just render blank */}
                    <Board>
                        {wishes.length ? wishes.map((item, i) => (<ListWishes wish={item}/>)) : "no wishes"}
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