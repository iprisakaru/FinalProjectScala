package by.bsu.utils

import by.bsu.model.dao.GithubUsersDAO
import by.bsu.model.repository.GithubUser

class GithubUserService(githubUsersDAO: GithubUsersDAO) {

  def checkByNode(githubUser: GithubUser) ={
    githubUsersDAO.insert(githubUser)
  }

}
