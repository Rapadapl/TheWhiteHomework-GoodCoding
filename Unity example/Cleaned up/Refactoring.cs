using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using Cinemachine;

public class Refactoring : MonoBehaviour
{
    [SerializeField]
    private CinemachineVirtualCamera cinemachineVirtualCamera;

    private float orthSize;
    private float targetOrthSize;

    private void Start()
    {
        orthSize = cinemachineVirtualCamera.m_Lens.OrthographicSize;
        targetOrthSize = orthSize;
    }
    private void Update()
    {
        Move();
        Zoom();
    }

    private void Move()
    {
        float x = Input.GetAxisRaw("Horizontal");
        float y = Input.GetAxisRaw("Vertical");

        Vector3 move = new Vector3(x,y,0).normalized;
        float speed = 30f ; 
        transform.position +=  move * speed * Time.deltaTime;

        float zoom = 2f;
        targetOrthSize -= Input.mouseScrollDelta.y * zoom;
    }
    private void Zoom()
    {
        float minOrth = 10; 
        float maxOrth = 30;
        
        targetOrthSize = Mathf.Clamp(targetOrthSize,minOrth,maxOrth);
        float zoomSpeed = 5f;
        orthSize = Mathf.Lerp(orthSize, targetOrthSize, Time.deltaTime * zoomSpeed);

        cinemachineVirtualCamera.m_Lens.OrthographicSize = orthSize;
    }
}
