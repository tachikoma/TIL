const Sequelize = require('sequelize');
const sequelize = new Sequelize('node_api_codelab', 'root', '1234', {dialect: 'mysql', host: 'localhost'})

const User = sequelize.define('user', {
    name: Sequelize.STRING
})

module.exports = {
    sequelize: sequelize,
    User: User
}