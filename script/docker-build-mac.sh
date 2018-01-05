docker login

REGISTRY_URL=sppark
CURRENT_DIR="$(dirname $(pwd))"

cd $CURRENT_DIR/iam-front-end
npm install
npm run build

cd $CURRENT_DIR/iam-sample-app
mvn package -B
docker build -t $REGISTRY_URL/uengine-iam:v1 ./
docker push $REGISTRY_URL/uengine-iam:v1










