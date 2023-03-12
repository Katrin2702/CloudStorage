FROM node:14
EXPOSE 8080
ADD ./ /frontend
WORKDIR /frontend
RUN npm install
ENTRYPOINT ["npm", "run", "serve"]


#WORKDIR /app
#COPY package*.json ./
#RUN npm install
#COPY . .
#EXPOSE 8080
#CMD ["npm", "run", "serve"]

#EXPOSE 8080
#
#COPY . .
#
#RUN npm install
#
#CMD ["npm", "run", "serve"]