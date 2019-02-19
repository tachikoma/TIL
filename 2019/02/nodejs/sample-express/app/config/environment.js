const environments = {
    development: {
        mysql: {
            username: 'root',
            password: '1234',
            database: 'node_api_codelab_dev'
        }
    },
    test: {
        mysql: {
            username: 'root',
            password: '1234',
            database: 'node_api_codelab_test'
        }
    },
    production: {

    }
} 

const nodeEnv = process.env.NODE_ENV || 'development'

module.exports = environments[nodeEnv]