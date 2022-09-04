# pollrestAPI

REST API для организации работы бассейна 
  
Дискретность записи - 1 час

- Возможность записаться только на рабочее время
- Ограничение на количество записей в час (не более 10 человек)
- Поиск записей (по имени, дате посещения)
- Отдельные графики для праздничных дней
- Ограничение на количество записей в день на человека (не более двух посещения в день)
- Возможность записи на несколько часов подряд

### API для работы с клиентами

#### 1. GET getClients (/api/v0/pool/client/all)

##### Описание 
Получение списка клиентов бассейна

##### Структура ответа
```
{[
    "id": number,
    "name": string
]}
```

#### 2. GET getClient (/api/v0/pool/client/get)

##### Описание
Получение данных о клиенте 

##### Входные данные
```
    id: number
```

##### Структура ответа
```
{
    "id": number,
    "name": string,
    "phone": string,
    "email": string
}
```

#### 3. POST addClient (/api/v0/pool/client/add)

##### Описание
Добавление нового клиента

##### Структура входных данных (body) 
```
{
    "name": string,
    "phone": string,
    "email": string
}    
```

#### 4. POST updateClient (/api/v0/pool/client/update)

##### Описание
Обновление данных о клиенте

##### Структура входных данных (body)
```
{
    "id": number
    "name": string,
    "phone": string,
    "email": string
}    
```

### API для работы с записями

#### 1. GET getAll (/api/v0/pool/timetable/all)

##### Описание
Получение занятых записей на определенную дату

##### Входные данные
```
    date: string
```

##### Структура ответа
```
{[
    "time": string,
    "count": number //Количество занятых записей в указанное время
]}
```

#### 2. GET getAvailable (/api/v0/pool/timetable/available)

##### Описание
Получение доступных записей на определенную дату

##### Входные данные
```
    date: string
```

##### Выходные данные
```
{[
    "time": string,
    "count": number //Количество доступных записей в указанный время 
]}
```

#### 3. POST reserve (/api/v0/pool/timetable/reserve)

##### Описание
Добавить запись клиента на определенное время

##### Структура входных данных (body)
```
{
    "clientId": number,
    "datetime": string
}
```

##### Выходные данные
```
{
    "orderId": string //Идентификатор засиси
}
```

#### 4. GET cancel (/api/v0/pool/timetable/cancel)

##### Описание
Отмена записи клиента на определенное время

##### Структура входных данных (body)
```
{
    "clientId": number
    "orderId": string //Идентификатор записи
}
```

#### 5. GET (/api/v0/pool/timetable/cancel/by_name)

##### Описание
Получение списка всех записей клиента с определенным именем

##### Входные данные
```
    name: string
```

##### Выходные данные
```
{[
    "id": number,
    "name": string
    "orderId": string
    "date": string
    "time": string
]}
```

#### 6. GET (/api/v0/pool/timetable/cancel/by_date)

##### Описание
Получение списка всех записей клиентов на определенную дату

##### Входные данные
```
    date: string
```

##### Выходные данные
```
{[
    "id": number,
    "name": string
    "orderId": string
    "date": string
    "time": string
]}
```
