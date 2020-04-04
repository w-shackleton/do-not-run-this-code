#include "uk_digitalsquid_physicsland_Box2DInterface.h"
#include "box2d-trunk-27062009/Include/Box2D.h"
#include "stdlib.h"
#include "stdio.h"
#include <jni.h>

int32 maxBodySize=300;
b2Body *bodies[300];//i have no idea
b2World *world = NULL;

// Prepare for simulation. Typically we use a time step of 1/60 of a
// second (60Hz) and 10 iterations. This provides a high quality simulation
// in most game scenarios.
float32 timeStep = 1.0f / 15.0f;
int32 velocityIterations = 8; // 8
int32 positionIterations = 1; // 1

int32 bodyIndex=0;





extern "C" {



	int32 findId(b2Body *body){
		for(int32 i=0;i<maxBodySize;i++){
			if(bodies[i]==body){
				return i;
			}
		}
		return -1;
	}


	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    createWorld
	 * Signature: (FFFFFF)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_createWorld
	  (JNIEnv* env, jobject caller, jfloat minX, jfloat minY, jfloat maxX, jfloat maxY, jfloat gravityX, jfloat gravityY){

		//clear
		if(world!=NULL){
			for(int32 i=0;i<maxBodySize;i++){
				if(bodies[i]!=NULL){
					world->DestroyBody(bodies[i]);
					bodies[i]=NULL;
				}

						}
			world=NULL;

		}
		bodyIndex=0;

		b2AABB worldAABB;
				worldAABB.lowerBound.Set(minX, minY);
				worldAABB.upperBound.Set(maxX, maxY);

				// Define the gravity vector.
				b2Vec2 gravity(gravityX,gravityY);

				// Do we want to let bodies sleep?
				bool doSleep = false;//

				// Construct a world object, which will hold and simulate the rigid bodies.
				world = new b2World(worldAABB, gravity, doSleep);
	}


	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    createBox
	 * Signature: (FFFF)I
	 */
	JNIEXPORT jint JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_createBox
	  (JNIEnv* env, jobject caller, jfloat x, jfloat y, jfloat width, jfloat height){

		b2BodyDef groundBodyDef;
				groundBodyDef.position.Set(x+width/2, y+height/2);

				// Call the body factory which allocates memory for the ground body
				// from a pool and creates the ground box shape (also from a pool).
				// The body is also added to the world.
				b2Body* groundBody = world->CreateBody(&groundBodyDef);

				// Define the ground box shape.
				b2PolygonDef groundShapeDef;

				// The extents are the half-widths of the box.
				groundShapeDef.SetAsBox(width/2, height/2);

				// Add the ground shape to the ground body.
				groundBody->CreateFixture(&groundShapeDef);

				bodies[bodyIndex]=groundBody;
		return bodyIndex++;
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    createCircle
	 * Signature: (FFFFF)I
	 */
	JNIEXPORT jint JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_createCircle
	  (JNIEnv* env, jobject caller, jfloat x, jfloat y, jfloat radius){
		b2BodyDef ballDef;
					ballDef.position.Set(
							x,y
					);
					b2Body* body = world->CreateBody(&ballDef);

					b2CircleDef circleDef;
					circleDef.radius = radius;

					body->CreateFixture(&circleDef);
					bodies[bodyIndex]=body;
		return bodyIndex++;
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    createCircle
	 * Signature: (FFFFF)I
	 */
	JNIEXPORT jint JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_createCircle2
	  (JNIEnv* env, jobject caller, jfloat x, jfloat y, jfloat radius, jfloat weight, jfloat restitution){
		b2BodyDef ballDef;
					ballDef.position.Set(
							x,y
					);
					b2Body* body = world->CreateBody(&ballDef);

					b2CircleDef circleDef;
					circleDef.radius = radius;
					circleDef.density = weight;
					circleDef.restitution = restitution;

					body->CreateFixture(&circleDef);
					body->SetMassFromShapes();
					bodies[bodyIndex]=body;
		return bodyIndex++;
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    getBodyInfo
	 * Signature: (Lcom/akjava/android/box2d/BodyInfo;I)Lcom/akjava/android/box2d/BodyInfo;
	 */
	JNIEXPORT jobject JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_getBodyInfo
	  (JNIEnv* env, jobject caller, jobject bodyInfo, jint bindex){
if(bodies[bindex]==NULL){
	return NULL;
}
		jclass ballView = env->GetObjectClass(bodyInfo);
				jmethodID setValuesId = env->GetMethodID(ballView, "setValues", "(FFF)V");
					b2Body *body = bodies[bindex];
					env->CallVoidMethod(bodyInfo, setValuesId, body->GetPosition().x, body->GetPosition().y, body->GetAngle());



		return bodyInfo;
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    step
	 * Signature: (FII)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_step
	  (JNIEnv *env, jobject caller, jfloat timeStep, jint velocityIterations, jint positionIterations){
		world->Step(timeStep, velocityIterations, positionIterations);
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    setGravity
	 * Signature: (FF)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_setGravity
	  (JNIEnv *env, jobject caller, jfloat gravityX, jfloat gravityY){
		b2Vec2 gravity(gravityX,gravityY);
		world->SetGravity(gravity);
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    destroyBody
	 * Signature: (I)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_destroyBody
	  (JNIEnv *env, jobject caller, jint id){

		if(bodies[id]!=NULL){
		world->DestroyBody(bodies[id]);
		bodies[id]=NULL;
		}

	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    getCollisions
	 * Signature: (Lcom/akjava/android/box2d/collisionIdKeeper;I)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_getCollisions
	  (JNIEnv *env, jobject caller, jobject keeper, jint target){

		jclass ballView = env->GetObjectClass(keeper);
		jmethodID addId = env->GetMethodID(ballView, "add", "(I)V");

		b2ContactEdge* c=bodies[target]->GetConactList();
				while(c!=NULL){
					int id=findId(c->other);
					if(id!=-1){
						env->CallVoidMethod(keeper, addId, id);
					}
					c=c->next;
				}

	}


	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    createBox
	 * Signature: (FFFFFFF)I
	 */
	JNIEXPORT jint JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_createBox2
	  (JNIEnv *env, jobject caller, jfloat x, jfloat y, jfloat width, jfloat height, jfloat density, jfloat restitution, jfloat friction){
		b2BodyDef groundBodyDef;
						groundBodyDef.position.Set(x+width/2, y+height/2);

						// Call the body factory which allocates memory for the ground body
						// from a pool and creates the ground box shape (also from a pool).
						// The body is also added to the world.
						b2Body* groundBody = world->CreateBody(&groundBodyDef);


						// Define the ground box shape.
						b2PolygonDef groundShapeDef;
						groundShapeDef.density = density;
						groundShapeDef.restitution = restitution;
						groundShapeDef.friction = friction;


						// The extents are the half-widths of the box.
						groundShapeDef.SetAsBox(width/2, height/2);

						// Add the ground shape to the ground body.
						groundBody->CreateFixture(&groundShapeDef);
						groundBody->SetMassFromShapes();
						bodies[bodyIndex]=groundBody;
				return bodyIndex++;

	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    setBodyXForm
	 * Signature: (IFFF)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_setBodyXForm
	  (JNIEnv *env, jobject caller, jint id, jfloat x, jfloat y, jfloat angle){
		if(bodies[id]!=NULL){
		b2Vec2 vec(x,y);
		bodies[id]->SetXForm(vec,angle);
		}
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    setBodyAngularVelocity
	 * Signature: (IF)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_setBodyAngularVelocity
	  (JNIEnv *env, jobject caller, jint id, jfloat angle){
		if(bodies[id]!=NULL){
				bodies[id]->SetAngularVelocity(angle);
				}
	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    setBodyLinearVelocity
	 * Signature: (IFF)V
	 */
	JNIEXPORT void JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_setBodyLinearVelocity
	  (JNIEnv *env, jobject caller, jint id, jfloat x, jfloat y){
		if(bodies[id]!=NULL){
				b2Vec2 vec(x,y);
				bodies[id]->SetLinearVelocity(vec);
				}
	}



	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    getStatus
	 * Signature: (Lcom/akjava/android/box2d/BodyInfo;I)Lcom/akjava/android/box2d/BodyInfo;
	 */
	JNIEXPORT jobject JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_getStatus
	  (JNIEnv *env, jobject caller, jobject bodyInfo , jint bindex){

		if(bodies[bindex]==NULL){
			return NULL;
		}
				jclass ballView = env->GetObjectClass(bodyInfo);
						jmethodID setValuesId = env->GetMethodID(ballView, "setStatus", "(ZZZZZ)V");
							b2Body *body = bodies[bindex];
							env->CallVoidMethod(bodyInfo, setValuesId, body->IsBullet(), body->IsSleeping(), body->IsFrozen(),body->IsDynamic(),body->IsStatic());



				return bodyInfo;

	}

	/*
	 * Class:     com_akjava_android_box2d_NDKBox2dControler
	 * Method:    getLinerVelocity
	 * Signature: (Lcom/akjava/android/box2d/BodyInfo;I)Lcom/akjava/android/box2d/BodyInfo;
	 */
	JNIEXPORT jobject JNICALL Java_uk_digitalsquid_physicsland_Box2DInterface_getLinerVelocity
	  (JNIEnv *env, jobject caller, jobject bodyInfo, jint bindex){


	if(bodies[bindex]==NULL){
		return NULL;
	}
			jclass ballView = env->GetObjectClass(bodyInfo);
					jmethodID setValuesId = env->GetMethodID(ballView, "setLinerVelocity", "(FF)V");
						b2Body *body = bodies[bindex];
						env->CallVoidMethod(bodyInfo, setValuesId, body->GetLinearVelocity().x, body->GetLinearVelocity().y);



			return bodyInfo;
	}



}
