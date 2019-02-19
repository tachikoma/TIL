var express = require('express');
var router = express.Router();
const models = require('../models')

exports.create = (req, res) => {
  const name = req.body.name || ''
  if (!name.length) {
    return res.status(400).json({error: 'Incorrect name'})
  }

  models.User.create({
    name: name
  }).then((user)=> res.status(201).json(user))
}

exports.index = (req, res) => {
  models.User.findAll().then(users => res.json(users))
}

exports.show = (req, res) => {
  const id = parseInt(req.params.id, 10)
  if (!id) {
    return res.status(400).json({error:'Incorrect id'})
  }

  models.User.findOne({
    where: {
      id: id
    }
  }).then(user => {
    if (!user) {
      return res.status(404).json({error: 'No User'})
    }

    return res.json(user)
  })
}

exports.destroy = (req, res) => {
  const id = parseInt(req.params.id, 10)
  if (!id) {
    return res.status(400).json({error: 'Incorrect id'})
  }

  models.User.destroy({
    where: {
      id: id
    }
  }).then(()=> res.status(204).send())
}

exports.update = (req, res) => {
  res.send()
} 

let users = [
  {
    id: 1,
    name: 'alice'
  },
  {
    id: 2,
    name: 'bek'
  },
  {
    id: 3,
    name: 'chris'
  }
]

/* GET users listing. */
router.get('/', function(req, res, next) {
  // res.send('respond with a resource limit=' + req.query.limit + ", skip=" + req.query.skip);
  console.log('no id ' + req.path)
  res.json(users)
});

router.get('/:id', (req, res, next) => {
  console.log('with id ' + req.path)
  const id = parseInt(req.params.id, 10);
  if (!id) {
    return res.status(400).json({error: 'Incorrect id'});
  }

  let user = users.filter(user => user.id === id)[0]
  if (!user) {
    return res.status(404).json({error: 'Unknown user'});
  }

  res.json(user)
});

router.delete('/:id', (req, res) => {
  const id = parseInt(req.params.id, 10);
  if (!id) {
    return res.status(400).json({error: 'Incorrect id'});
  }

  const userIdx = users.findIndex(user => user.id === id);
  if (userIdx === -1) {
    return res.status(404).json({error: 'Unknown user'});
  }

  users.splice(userIdx, 1);
  res.status(204).send();
});

router.post('/', (req, res, next) => {
  const name = req.body.name || '';

  if (!name.length) {
    return res.status(400).json({error: 'Incorrenct name'});
  }

  const id = users.reduce((maxId, user) => {
    return user.id > maxId ? user.id : maxId
  }, 0) + 1;

  const newUser = {
    id: id,
    name: name
  };

  users.push(newUser)
  res.send()
})

router.put('/:id', exports.update)

module.exports = router;
