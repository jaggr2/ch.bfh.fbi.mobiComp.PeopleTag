
/*
 * GET userlist page.
 */

exports.userlist = function(db) {
    return function(req, res) {
        db.collection('userlist').find().toArray(function (err, items) {
            res.json(items);
        })
    }
};

/*
 * GET to get a user.
 */

exports.getuser = function(db) {
    return function(req, res) {
        var userToGet = req.params.id;
        db.collection('userlist').findById(userToGet, function(err, result) {
            res.send(result);
        });
    }
};


/*
 * POST to create a new user.
 */

exports.adduser = function(db) {
    return function(req, res) {
        req.body.positionReportedAt = new Date();
        req.body.createdAt = new Date();
        req.body.updatedAt = new Date();
        db.collection('userlist').insert(req.body, function(err, records){
            res.send(
                (err === null) ? records[0] : { msg: err }
            );
        });
    }
};

/*
 * POST to update user.
 */

exports.updateuser = function(db) {
    return function(req, res) {
        var userToGet = req.params.id;
        db.collection('userlist').findById(userToGet, function(err, result) {

            if( err !== null) {
                res.send(404, { msg: 'User with ID ' + userToGet + ' not found!' });
                return;
            }

            if( req.body.displayName === undefined || req.body.displayName === null) {
                res.send(400, { msg: 'displayName can not be empty!' });
                return;
            }

            var updateStatement = { $set : { updatedAt: new Date() } };

            updateStatement.$set.displayName = req.body.displayName;

            if( req.body.currentLatitude !== undefined && req.body.currentLatitude !== null) {
                updateStatement.$set.currentLatitude = req.body.currentLatitude;
                updateStatement.$set.positionReportedAt = new Date();
            }

            if( req.body.currentLongitude !== undefined && req.body.currentLongitude !== null) {
                updateStatement.$set.currentLongitude = req.body.currentLongitude;
                updateStatement.$set.positionReportedAt = new Date();
            }

            db.collection('userlist').updateById(userToGet, updateStatement, { safe: true }, function(err, modifiedcount){

                res.send(
                    (err === null) ? { msg: modifiedcount } : { msg: err }
                );
            });

        });
    }
};


/*
 * DELETE to deleteuser.
 */
exports.deleteuser = function(db) {
    return function(req, res) {
        var userToDelete = req.params.id;
        db.collection('userlist').removeById(userToDelete, function(err, result) {
            res.send((result === 1) ? { msg: '' } : { msg:'error: ' + err });
        });
    }
};

exports.addpairing = function(db) {
    return function(req, res) {
        var firstUser = req.params.firstId;
        var secondUser = req.params.secondId;

        db.collection('userlist').findById(firstUser, function(err, result) {
            if( err !== null) {
                res.send(404, { msg: 'User with ID ' + firstUser + ' not found!' });
                return;
            }


            if( result.pairings === undefined ) {
                result.pairings = [];
            }

            var pos = result.pairings.map(function(e) { return e.withUser; }).indexOf(secondUser);
            if(pos !== -1) {
                result.pairings[pos] = { withUser: secondUser, createdAt: new Date() };
            }
            else {
                result.pairings.push({ withUser: secondUser, createdAt: new Date() });
            }

            db.collection('userlist').updateById(firstUser, { $set : { pairings: result.pairings } }, { safe: true }, function(err, modifiedcount){
                if( err !== null) {
                    res.send(400, { msg: 'Error while updating User ' + firstUser });
                    return;
                }

                db.collection('userlist').findById(secondUser, function(err, result) {
                    if( err !== null) {
                        res.send(404, { msg: 'User with ID ' + secondUser + ' not found!' });
                        return;
                    }


                    if( result.pairings === undefined ) {
                        result.pairings = [];
                    }

                    var pos = result.pairings.map(function(e) { return e.withUser; }).indexOf(firstUser);
                    if(pos !== -1) {
                        result.pairings[pos] = { withUser: firstUser, createdAt: new Date() };
                    }
                    else {
                        result.pairings.push({ withUser: firstUser, createdAt: new Date() });
                    }

                    db.collection('userlist').updateById(secondUser, { $set : { pairings: result.pairings } }, { safe: true }, function(err, modifiedcount){

                        res.send(
                            (err === null) ? { msg: modifiedcount } : { msg: err }
                        );
                    });
                });
            });
        });
    }
};

exports.deletepairing = function(db) {
    return function(req, res) {
        var firstUser = req.params.firstId;
        var secondUser = req.params.secondId;

        db.collection('userlist').findById(firstUser, function(err, result) {
            if( err !== null) {
                res.send(404, { msg: 'User with ID ' + firstUser + ' not found!' });
                return;
            }


            if( result.pairings === undefined ) {
                result.pairings = [];
            }

            var pos = result.pairings.map(function(e) { return e.withUser; }).indexOf(secondUser);
            if(pos !== -1) {
                result.pairings.splice(pos, 1);

                db.collection('userlist').updateById(firstUser, { $set : { pairings: result.pairings } }, { safe: true }, function(err, modifiedcount){
                    if( err !== null) {
                        res.send(400, { msg: 'Error while updating User ' + firstUser });
                        return;
                    }

                    db.collection('userlist').findById(secondUser, function(err, result) {
                        if( err !== null) {
                            res.send(404, { msg: 'User with ID ' + firstUser + ' not found!' });
                            return;
                        }

                        if( result.pairings === undefined ) {
                            result.pairings = [];
                        }

                        var pos = result.pairings.map(function(e) { return e.withUser; }).indexOf(firstUser);
                        if(pos !== -1) {
                            result.pairings.splice(pos, 1);

                            db.collection('userlist').updateById(secondUser, { $set : { pairings: result.pairings } }, { safe: true }, function(err, modifiedcount){

                                res.send(
                                    (err === null) ? { msg: modifiedcount } : { msg: err }
                                );
                            });
                        }
                    });
                });
            }
        });
    }
};

exports.getPairedWith = function(db) {
    return function(req, res) {


        var userToGet = req.params.id;
        db.collection('userlist').findById(userToGet, function(err, theUser) {
            if( err !== null) {
                res.send(404, { msg: 'User with ID ' + firstUser + ' not found!' });
                return;
            }

            var ObjectID = require('mongoskin').ObjectID

            var theIDs = theUser.pairings.map(function(e) { return new ObjectID(e.withUser); });

            db.collection('userlist').find({
                '_id': { $in: theIDs}
            }).toArray(function(err, users) {
                res.send(users);
            });
        });
    }
};
