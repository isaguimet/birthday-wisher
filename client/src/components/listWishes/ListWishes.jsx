import BirthdayCard from "../birthdayCard/BirthdayCard";

const ListWishes = (props) => {
    return (

        <BirthdayCard>
            {props.wish}
        </BirthdayCard>

    //     <div className="Component"> 
    
    //     <h1>{props.text}</h1> 
        
    //   </div> 
    );

}

export default ListWishes;