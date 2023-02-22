import {useDispatch, useSelector} from "react-redux";
import {getFriends} from "../store/user";
import {useEffect} from "react";
import Theme from "../theme/Theme";
import FriendCard from "../components/friendCard/FriendCard";
import {StyledDiv} from "./ProfilePage.style";
import {FriendList} from "../components/friendCard/FriendCard.style";

const FriendsPage = (props) => {

    const dispatch = useDispatch();
    const friend = useSelector(state => state.user);
    console.log("friends: " + JSON.stringify(friend.user.data));

    useEffect(() => {
        dispatch(getFriends())
    }, []);

    return (
        <Theme>
            <StyledDiv>
                <FriendList>
                    <h1>Friends list</h1>
                    {friend.user.data.map((friend) => (
                        <FriendCard
                            key={friend.id}
                            id={friend.id}
                            firstName={friend.firstName}
                            lastName={friend.lastName}
                            birthdate={friend.birthdate}
                            profilePic={friend.profilePic}
                        />
                    ))}
                </FriendList>

            </StyledDiv>
        </Theme>
    );
};

export default FriendsPage;