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

    // const [wishes, setWishes] = useState([]);
    // const [wishMessage, setWishMessage] = useState('');
    // const [updated, setUpdated] = useState(wishMessage); 

    // const handleChange = (event) => {
    //     setWishMessage(event.target.value);
    //   };
    
    // const handleClick = () => {
    //     setUpdated(wishMessage);
    // };
    

    // const addWish = () => {
    //     setWishes([...wishes, updated])
    // }

    const getBirthday = () => {
        // var bd = props.birthday + "Z"
        let bday = new Date(props.birthday);
        let today = new Date();

        let diff = today.getTime() - bday.getTime();
        let age = Math.floor(diff/(1000*3600*24*365))
        return (
            <>{age}</>
        );
        
    }

    // ----
    const dispatch = useDispatch();
    const board = useSelector(state => state.board);

    useEffect(() => {
        dispatch(getBoards())
    }, []);

    return (
        <Theme>
            <StyledDiv>
                <Profile>

                    <Container>
                        <Row>
                            <Col sm={2}><ProfilePic src={Icons[4]}/></Col>
                            <Col>
                                <Container>
                                    <Row>
                                        <h1>{props.name} {getBirthday()}
                                        </h1>
                                    </Row>
                                    <Row>
                                        <h4>{props.birthday}</h4> 
                                    </Row>
                                </Container>
                            </Col>
                            <Col>
                                <a href="/settings">
                                Settings
                                <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-settings" width="30" height="30" viewBox="0 0 24 24" stroke-width="1.5" stroke="#2c3e50" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                        <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                                        <path d="M10.325 4.317c.426 -1.756 2.924 -1.756 3.35 0a1.724 1.724 0 0 0 2.573 1.066c1.543 -.94 3.31 .826 2.37 2.37a1.724 1.724 0 0 0 1.065 2.572c1.756 .426 1.756 2.924 0 3.35a1.724 1.724 0 0 0 -1.066 2.573c.94 1.543 -.826 3.31 -2.37 2.37a1.724 1.724 0 0 0 -2.572 1.065c-.426 1.756 -2.924 1.756 -3.35 0a1.724 1.724 0 0 0 -2.573 -1.066c-1.543 .94 -3.31 -.826 -2.37 -2.37a1.724 1.724 0 0 0 -1.065 -2.572c-1.756 -.426 -1.756 -2.924 0 -3.35a1.724 1.724 0 0 0 1.066 -2.573c-.94 -1.543 .826 -3.31 2.37 -2.37c1 .608 2.296 .07 2.572 -1.065z" />
                                        <circle cx="12" cy="12" r="3" />
                                </svg>
                                </a>
                            </Col>
                        </Row>
                    </Container>
                </Profile>
                {board.loading && <div>Loading...</div>}
                {!board.loading && board.error ? <div>Error: {board.error}</div> : null}
                {!board.loading && board.data ? (
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