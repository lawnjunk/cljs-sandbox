const {faker} = require('@faker-js/faker')
const uuid = require('uuid')
const cors = require('cors')
const express = require('express')
const onFinished = require('on-finished')

let store = {}

const sleep = (delayInMS) => {
  return new Promise((resolve) => {
    setTimeout(() => resolve(), delayInMS)
  })
}

const parseIntOrDefault = (value, DEFAULT) => {
  console.log(`value`, value, typeof value)
  if (typeof value === 'number') {
    return value
  }
  try {
    let result = parseInt(value)
    if (isNaN(result)){
      return DEFAULT
    }
    return result
  } catch (_error) {
    return DEFAULT
  }
}

let storeItemCreate = (name) => {
  let item = {
    id: uuid.v4().slice(0, 5),
    createdAt: new Date(),
    name,
    completed: false,
  }
  store[item.id] = item
  return item
}

let storeItemExists = (id) => {
  if (store[id]) {
    return true
  }
  return false
}

/** returns boolean (true if success)*/
let storeItemDelete = (id) => {
  delete store[id]
}

let storeItemComplete = (id) => {
  store[id].completed = true
}

let app = express()

app.use(express.json())
app.use(cors('*'))
app.use((req, res, next) => {
  let startTime = Date.now()
    console.log()
    console.log(`(start) ${req.method} ${req.url}`)
  onFinished(res, () => {
    let deltaTime = Date.now() - startTime
    console.log(`(${deltaTime}ms) ${res.statusCode} ${req.method} ${req.url}`)

    let itemList = Object.values(store)
    if (itemList.length === 0){
      console.log('-- store is empty')
    } else {
      console.log('-- store: ')
      itemList.forEach((item) => {
        console.log(`---- ${item.id} completed(${item.completed}) ${item.name}`)
      })
    }
  })
  next()
})

app.post('/api/spinner', async (req, res) => {
  let delayInMS = req.body.delayInMS || Math.floor(Math.random() * 2000) + 200
  await sleep(delayInMS)
  res.json({
    content: faker.lorem.words(3),
    imageUrl: "https://picsum.photos/200/200.jpg?grayscale&rand=" + 
    faker.datatype.hexadecimal({length: 5}),
  })
})

app.post('/api/debug', async (req, res) => {
  let {delayInMS, status, payload} = req.body
  console.dir(req.body)
  delayInMS = parseIntOrDefault(delayInMS, Math.floor(Math.random() * 500) + 100) 
  status = parseIntOrDefault(status, 200)
  console.log({delayInMS, status, payload})
  await sleep(delayInMS)

  if (payload) {
    console.log("sending payload")
    if (payload?.content) {
      payload.content = payload.content.toUpperCase() + `!!`
    }
    return res.status(status).json({
      ...payload,
      luckNumber: Math.random(),
      example: {
        cool: "beans",
        list: [
          {nice: "one"},
          {nice: "two"},
          {nice: "three"},
        ]
      }
    })
  } else {
    res.sendStatus(status)
  }
})

app.post('/api/item-list-fetch', (_req, res) => {
  res.json(Object.values(store))
})

app.post('/api/item-create', (req, res) => {
  let {name} = req.body
  if (!name || typeof name != 'string') 
    return res.status(400).json({error: 'name is required or invaild'})
  let item = storeItemCreate(name)
  res.json(item)
})

app.post('/api/item-delete', (req, res) => {
  let {id} = req.body
  if (!id|| typeof id != 'string') 
    return res.status(400).json({error: 'id required or invaild'})
  if (!storeItemExists(id)) {
    return res.status(404).json({error: 'item not found'})
  }
  storeItemDelete(id)
  res.sendStatus(200)
})

app.post('/api/item-complete', (req, res) => {
  let {id} = req.body
  if (!id|| typeof name != 'string') 
    return res.status(400).json({error: 'name is required or invaild'})
  if (!storeItemExists(id)) {
    return res.status(404).json({error: 'item not found'})
  }
  res.json(storeItemComplete(id))
})

app.listen(7766, () => {
  console.log('server running on port 7766')
})
