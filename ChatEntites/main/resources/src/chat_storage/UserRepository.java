package chat_storage;

import chat_enty.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract interface UserRepository extends JpaRepository<User, Integer> {

}
