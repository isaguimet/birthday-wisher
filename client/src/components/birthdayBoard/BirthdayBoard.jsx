import {Board, BoardContainer} from "../../pages/ProfilePage.style";
import ListWishes from "../listWishes/ListWishes";
import BirthdayCard from "../birthdayCard/BirthdayCard";
import BootstrapSwitchButton from "bootstrap-switch-button-react";
import {useState} from "react";

/**
 * A component for rendering a User Birthday Board.
 * @param props {Object} Expected props for this component:
 * - boardId {string} the unique identifier for this board in the database.
 * - year {string} the year that this board is for.
 * - open {boolean} whether the board is open or closed (for people to submit messages to).
 * - public {boolean} whether the board is public or private (visibility from friends).
 * - messages {object} birthday messages that belong to this board.
 * @returns {JSX.Element}
 * @constructor
 */
const BirthdayBoard = (props) => {
    const [isOpen, setOpen] = useState(props.open === true);
    const [isPublic, setPublic] = useState(props.public === true);

    const togglePublic = () => {
        // TODO: make api call to reflect change in db
        setPublic(!isPublic);
    }

    const toggleOpen = () => {
        // TODO: make api call to reflect change in db
        setOpen(!isOpen);
    }

    return (
        <BoardContainer>
            {props.year}

            <button onClick={() => {}}>Add Wish</button>

            {/*TODO: only make these switches visible if this is viewed on the profile page of the current user*/}
            <BootstrapSwitchButton
                id={"public_toggle"}
                checked={isPublic}
                onlabel={"Public"}
                offlabel={"Private"}
                onChange={togglePublic}
            />
            <BootstrapSwitchButton
                id={"open_toggle"}
                checked={isOpen}
                onlabel={"Open"}
                offlabel={"Closed"}
                onChange={toggleOpen}
            />

            {/* if no item it doesn't render anything, if no item it should just render blank */}
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
    )
};

export default BirthdayBoard;