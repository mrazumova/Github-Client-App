# Github-Client-App

Десктопный git-клиент

## Установка
Скачайте jar-файл. Команда для запуска из командной строки:

```bash
java -jar git-client.jar
```

## Использование

Войдите в свой аккаунт

![Login Page](/readme/login.PNG)

![Home Page](/readme/home1.PNG)

После выбора репозитория в списке слева отображается информация о нём. 

Также доступны следующие действия:

* Удалить репозиторий

* Сконировать на устройство

* Открыть для просмотра в браузере

* Поиск по имени репозитория

![Remote Repo Actions](/readme/home2.PNG)

На вкладке Local Actions доступны действия с локальными репозиториями, а именно:

* Init repository

 Инициализация локального репозитория
 
> После нажатия кнопки 'Init' пользователю будет предложено выбрать папку для создания локального репозитория. 
> После выбора будет создан локальный git-репозиторий.

* Commit
  
> После нажатия кнопки 'Commit' пользователю нужно выбрать репозиторий (.git) из файловой системы. 
> Далее будет предложено ввести сообщение коммита. После этого будет создан коммит, содержащий все изменения в данном репозитории.

* Add file

 Добавление файла в git.
 
> После нажатия кнопки 'Add' пользователю нужно выбрать репозиторий(.git), в который нужно добавить файл.
> Далее система предлагает выбрать файл, который нужно добавить в систему контроля версий.

* Push to repository

> После нажатия кнопки 'Push' пользователю нужно выбрать репозиторий(.git). Локальный репозиторий должен быть связан с удаленным. Если это не так, будет выведено сообщение об ошибке Чтобы решить данную проблему, нужно воспользоваться кнопкой 'Link'. Если же всё успешно, то последние изменения появятся в удаленном репозитории.

* Link repositories

 Связать локальный и удаленный репозиторий
 
 > После нажатия кнопки 'Link' пользователю нужно ввести название существующего локального репозитория. Далее нужно выбрать локальный репозиторий.

![Local Repo Actions](/readme/actions.PNG)

На вкладках Followers и Following доступны подписчики и подпски.