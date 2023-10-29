// eslint-disable-next-line react-refresh/only-export-components,react/prop-types
const UserProfile = ({name, age, gender,imageNumber,...props}) => {
    return (
        <div>
            <h1>{name}</h1>
            <p>{age}</p>
            <img src={`https://randomuser.me/api/portraits/med/${gender}/${imageNumber}.jpg`} />
            {props.children}
        </div>
    )
}

export default UserProfile;

