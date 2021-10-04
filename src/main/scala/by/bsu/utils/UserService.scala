package by.bsu.utils

import by.bsu.model.dao.UsersDAO
import by.bsu.model.repository.User

class UserService(githubUsersDAO: UsersDAO) {

  def checkGithubUserByNode(githubUser: User) = {
    githubUsersDAO.insertGithub(githubUser)
  }

  def checkGoogleUserById(user: User) = {
    githubUsersDAO.insertGoogle(user)
  }

}
