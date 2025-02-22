; ----------------------------------------
; Name : Newton Fractal By Filax/CreepyCat
; Date : (C)2025 
; Site : https://github.com/BlackCreepyCat
; ----------------------------------------
Graphics 800, 600, 32, 2 ; Résolution 800x600, mode fenêtre
SetBuffer BackBuffer()

; Paramètres de la fractale
Global imgWidth = 800
Global imgHeight = 600
Global maxIter = 50
Global tolerance# = 0.0001
Global zoomLevel# = 4.0 ; Échelle initiale du plan complexe [-zoomLevel, zoomLevel]
Global centerX# = 0.0   ; Centre du plan complexe (partie réelle)
Global centerY# = 0.0   ; Centre du plan complexe (partie imaginaire)

; Créer une image pour la fractale
Global newtonImage = CreateImage(imgWidth, imgHeight)

; Racines de z^3 - 1
Global root1r# = 1.0, root1i# = 0.0
Global root2r# = -0.5, root2i# = 0.866
Global root3r# = -0.5, root3i# = -0.866

; Générer la fractale de Newton
Function ComputeNewtonFractal()
    LockBuffer ImageBuffer(newtonImage)
    
    For x = 0 To imgWidth - 1
        For y = 0 To imgHeight - 1
            Local zr# = centerX + (x / Float(imgWidth) - 0.5) * zoomLevel
            Local zi# = centerY + (y / Float(imgHeight) - 0.5) * zoomLevel
            
            Local iter = 0
            Local converged = False
            Repeat
                Local zr2# = zr * zr
                Local zi2# = zi * zi
                Local zrsq_plus_zisq# = zr2 + zi2
                
                If zrsq_plus_zisq > 1000000.0  Then
                    converged = True
                EndIf
                
                Local fzr# = zr * zr2 - 3 * zr * zi2 - 1
                Local fzi# = 3 * zr2 * zi - zi * zi2
                
                Local dfzr# = 3 * (zr2 - zi2)
                Local dfzi# = 6 * zr * zi
                
                Local denom# = dfzr * dfzr + dfzi * dfzi
                If denom < 0.000001 Then
                    converged = True
                Else
                    Local deltaZr# = (fzr * dfzr + fzi * dfzi) / denom
                    Local deltaZi# = (fzi * dfzr - fzr * dfzi) / denom
                    zr = zr - deltaZr
                    zi = zi - deltaZi
                EndIf
                
                Local dist1# = Sqr((zr - root1r) * (zr - root1r) + (zi - root1i) * (zi - root1i))
                Local dist2# = Sqr((zr - root2r) * (zr - root2r) + (zi - root2i) * (zi - root2i))
                Local dist3# = Sqr((zr - root3r) * (zr - root3r) + (zi - root3i) * (zi - root3i))
                
                If dist1 < tolerance Or dist2 < tolerance Or dist3 < tolerance Then
                    converged = True
                EndIf
                iter = iter + 1
            Until iter >= maxIter Or converged
            
            Local r#, g#, b#
            Local intensity# = 255 * (1.0 - Float(iter) / maxIter)
            If intensity < 0 Then intensity = 0
            
            If converged And iter < maxIter Then
                If dist1 < tolerance Then
                    r = intensity
                    g = 0
                    b = 0
                ElseIf dist2 < tolerance Then
                    r = 0
                    g = intensity
                    b = 0
                ElseIf dist3 < tolerance Then
                    r = 0
                    g = 0
                    b = intensity
                EndIf
            Else
                r = 0
                g = 0
                b = 0
            EndIf
            
            Local colorValue = (r Shl 16) Or (g Shl 8) Or b
            WritePixelFast x, y, colorValue, ImageBuffer(newtonImage)
        Next
    Next
    
    UnlockBuffer ImageBuffer(newtonImage)
End Function

; Boucle principale avec gestion du zoom
ComputeNewtonFractal() ; Premier calcul

Repeat
        Local mx = MouseX()
        Local my = MouseY()

    If MouseHit(1) Then ; Clic gauche pour zoomer

        ; Convertir la position de la souris en coordonnées du plan complexe
        newCenterX# = centerX + (mx / Float(imgWidth) - 0.5) * zoomLevel
        newCenterY# = centerY + (my / Float(imgHeight) - 0.5) * zoomLevel
        centerX = newCenterX
        centerY = newCenterY
        zoomLevel = zoomLevel * 0.5 ; Zoom avant (réduit l'échelle)
        ComputeNewtonFractal() ; Recalculer
    EndIf
    
    If MouseHit(2) Then ; Clic droit pour dézoomer
         mx = MouseX()
       my = MouseY()
        newCenterX# = centerX + (mx / Float(imgWidth) - 0.5) * zoomLevel
        newCenterY# = centerY + (my / Float(imgHeight) - 0.5) * zoomLevel
        centerX = newCenterX
        centerY = newCenterY
        zoomLevel = zoomLevel * 2.0 ; Zoom arrière (augmente l'échelle)
        ComputeNewtonFractal() ; Recalculer
    EndIf
    
    Cls
    DrawImage newtonImage, 0, 0
    Flip
Until KeyHit(1) ; Quitter avec Échap

; Libérer la mémoire
FreeImage newtonImage
End