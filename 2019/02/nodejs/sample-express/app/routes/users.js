var express = require('express');
var router = express.Router();
const models = require('../models')

exports.create = (req, res) => {
  const name = req.body.name || ''
  if (!name.length) {
    return res.status(400).json({
      error: 'Incorrect name'
    })
  }

  models.User.create({
    name: name
  }).then((user) => res.status(201).json(user))
}

exports.index = (req, res) => {
  models.User.findAll().then(users => res.json(users))
}

exports.show = (req, res) => {
  const id = parseInt(req.params.id, 10)
  if (!id) {
    return res.status(400).json({
      error: 'Incorrect id'
    })
  }

  models.User.findOne({
    where: {
      id: id
    }
  }).then(user => {
    if (!user) {
      return res.status(404).json({
        error: 'No User'
      })
    }

    return res.json(user)
  })
}

exports.destroy = (req, res) => {
  const id = parseInt(req.params.id, 10)
  if (!id) {
    return res.status(400).json({
      error: 'Incorrect id'
    })
  }

  models.User.destroy({
    where: {
      id: id
    }
  }).then(() => res.status(204).send())
}

exports.update = (req, res) => {
  const id = parseInt(req.params.id, 10)
  if (!id) {
    return res.status(400).json({
      error: 'Incorrect id'
    })
  }

  models.User.findOne({
    where: {
      id: id
    }
  }).then(user => {
    if (!user) {
      return res.status(404).json({
        error: 'No User'
      })
    }

    models.User.update({
      name: req.body.name
    }, {
      where: {
        id: id
      }
    }).then((result) => res.json(result))
  })
}

/* GET users listing. */
router.get('/', exports.index);

router.get('/:id', exports.show);

router.delete('/:id', exports.destroy);

router.post('/', exports.create)

router.put('/:id', exports.update)

module.exports = router;