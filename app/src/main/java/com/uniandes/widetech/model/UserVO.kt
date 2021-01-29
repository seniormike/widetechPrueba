import com.uniandes.widetech.model.User

/**
 * Class that represents an User
 * @author Miguel Angel Puentes
 */
class UserVO(
    override val id: String,
    override val email: String
) : User {
}