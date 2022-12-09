const uuid = require('uuid')
const express = require('express')
const onFinished = require('on-finished')

let store = {}

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

app.listen(6666, () => {
  console.log('server running on port 6666')
})
