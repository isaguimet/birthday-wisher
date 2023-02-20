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
import BirthdayBoard from "../components/birthdayBoard/BirthdayBoard";


const ProfilePage = (props) => {

    const dispatch = useDispatch();
    const board = useSelector(state => state.board)

    useEffect(() => {
        dispatch(getBoards())
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
                {board.loading && <div>Loading...</div>}
                {!board.loading && board.error ? <div>Error: {board.error}</div> : null}
                {!board.loading && board.data.length ? (
                    <div>
                        {board.data.map((board) => (
                            <BirthdayBoard
                                key={board.id}
                                id={board.id}
                                boardId={board.id}
                                year={board.year}
                                open={board.open}
                                public={board.public}
                                messages={board.messages}
                            />
                        ))}
                    </div>
                ) : null}
            </StyledDiv>
        </Theme>
    );
};

export default ProfilePage;