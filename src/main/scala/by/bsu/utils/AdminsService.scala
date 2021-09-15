package by.bsu.utils

import by.bsu.model.dao.AdminsDAO
import by.bsu.model.repository.Admin
import com.github.t3hnar.bcrypt._
import scala.concurrent.ExecutionContext.Implicits.global

class AdminsService(adminsDAO: AdminsDAO) {

  def getPassword(username: String) = {
    adminsDAO.getPassword(username).map(_.password)
  }

  def create(admin: Admin) = {
    adminsDAO.insert(admin.copy(password = admin.password.bcrypt))
  }

}
