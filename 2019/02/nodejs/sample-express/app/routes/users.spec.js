const should = require('should')
const request = require('supertest')
const app = require('../app')
const syncDatabase = require('../bin/sync-database')
const models = require('../models');

describe('GET /users', () => {
    before('sync database', (done) => {
        syncDatabase().then(() => done())
    })
    // it('should return 200 status code', (done) => {
    //     request(app)
    //         .get('/users')
    //         .expect(200)
    //         .end((err, res) => {
    //             if (err) throw err
    //             done()
    //         })
    // })
    const users = [{
            name: 'alice'
        },
        {
            name: 'bek'
        },
        {
            name: 'chris'
        }
    ]
    before('insert 3 users into database', (done) => {
        models.User.bulkCreate(users).then(() => done())
    })

    after('clear up database', (done) => {
        syncDatabase().then(() => done());
    });

    it('should return array', (done) => {
        request(app).get('/users').expect(200).end((err, res) => {
            if (err) throw err
            res.body.should.be.an.instanceof(Array).and.have.length(3)
            res.body.map(user => {
                user.should.have.properties('id', 'name')
                user.id.should.be.a.Number()
                user.name.should.be.a.String()
            })
            done()
        })
    })
})

describe('PUT /users/:id', () => {

    before('', (done) => {
        models.User.create({
            name: "앨리스"
        }).then(() => done())
    })

    it('should return 200 status code', (done) => {
        request(app)
            .put('/users/1')
            .send({
                name: 'foo'
            })
            .expect(200)
            .end((err, res) => {
                if (err) throw err
                done()
            })
    })
})